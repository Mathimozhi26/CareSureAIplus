const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

// Route controllers
const { registerUser, authUser, getUserProfile, updateUserProfile } = require('./controllers/authController');
const { getProducts, getProductById, getIngredientDetails, seedDatabase } = require('./controllers/productController');
const { analyzeLabelImage } = require('./controllers/scanController');
const { sendChatMessage } = require('./controllers/chatController');

// Load environmental parameters
dotenv.config();

// Connect to MongoDB
connectDB();

const app = express();

// Secure middlewares
app.use(cors());
app.use(express.json({ limit: '10mb' })); // Support larger base64 label photos

// Request logging middleware
app.use((req, res, next) => {
  console.log(`[Clinical Diagnostics Tracker] ${req.method} request received on ${req.url}`);
  next();
});

// --- JWT User Context Security Verification ---
const protectUser = async (req, res, next) => {
  let token;
  const User = require('./models/User');
  const jwt = require('jsonwebtoken');

  if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
    try {
      token = req.headers.authorization.split(' ')[1];
      const decoded = jwt.verify(token, process.env.JWT_SECRET || 'CaresureClinicalSecuritySecretKey2026');
      req.user = await User.findById(decoded.id).select('-password');
      next();
    } catch (error) {
      console.error('[JWT Verification Failure]', error.message);
      res.status(401).json({ message: 'Not authorized, token verification failed' });
    }
  }

  if (!token) {
    res.status(401).json({ message: 'Not authorized, no clinical bearer token received' });
  }
};

// --- REST Endpoint Routings ---

// 1. Authentication
app.post('/api/auth/register', registerUser);
app.post('/api/auth/login', authUser);
app.get('/api/auth/profile', protectUser, getUserProfile);
app.put('/api/auth/profile', protectUser, updateUserProfile);

// 2. Catalogs & Science Database
app.get('/api/products', getProducts);
app.get('/api/products/:id', getProductById);
app.get('/api/ingredients/search', getIngredientDetails);
app.post('/api/products/seed', seedDatabase);

// 3. Multimodal Scanner
app.post('/api/scan/analyze', analyzeLabelImage);

// 4. CareBot AI Chatbot
app.post('/api/chat/message', sendChatMessage);

// Root informational endpoint
app.get('/', (req, res) => {
  res.send('CareSure AI+ Clinical Diagnosis API Service is Operational.');
});

// Server Bootstrapper
const PORT = process.env.PORT || 5000;

const startServer = () => {
  app.listen(PORT, () => {
    console.log(`[Express Web Engine] Server successfully mounted on port ${PORT}`);
  });
};

// Check for command argument --seed to execute data seeding
if (process.argv.includes('--seed')) {
  console.log('[Seeder Flag Detected] Initializing MongoDB seed transaction...');
  setTimeout(async () => {
    try {
      const Product = require('./models/Product');
      const Ingredient = require('./models/Ingredient');
      const { generateDataset } = require('./utils/datasetSeeder');
      const initialIngredients = require('./seedIngredients.json');

      await Product.deleteMany({});
      await Ingredient.deleteMany({});
      
      const dynamicProducts = generateDataset();
      await Product.insertMany(dynamicProducts);
      await Ingredient.insertMany(initialIngredients);

      console.log(`[Seeder Completed] Successfully populated database tables with ${dynamicProducts.length} products! Exiting seeder process.`);
      process.exit(0);
    } catch (err) {
      console.error(`[Seeder Core Failure] ${err.message}`);
      process.exit(1);
    }
  }, 1000);
} else {
  startServer();
}
