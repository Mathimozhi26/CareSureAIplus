# CareSure AI+ Full-Stack MERN Application

CareSure AI+ is a state-of-the-art diagnostic screening and safety intelligence platform designed to decode active ingredients, clinical formulations, and product labels in real-time. Features include a dynamic ingredient database, CareBot AI health advisors powered by Gemini, and personalized skin matrix safety scans.

---

## 📂 Project Architecture

```plain
web_export/
├── Backend/
│   ├── config/
│   │   └── db.js                 # MongoDB database link adapter
│   ├── controllers/
│   │   ├── authController.js     # User security controllers
│   │   ├── chatController.js     # CareBot Gemini Chat logic
│   │   ├── productController.js  # Database product listings
│   │   └── scanController.js     # AI label scanner engine
│   ├── models/
│   │   ├── Ingredient.js         # Decoded raw chemical schemas
│   │   ├── Product.js            # Sealed cosmetics & med catalogs
│   │   └── User.js               # Clinical security profile schemas
│   ├── seedIngredients.json      # Complete seed science ingredient catalog
│   ├── seedProducts.json         # Seeding product dataset
│   ├── .env.example
│   ├── package.json
│   └── server.js                 # Primary Express server routing
├── Frontend/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── Header.jsx
│   │   │   └── CustomCard.jsx
│   │   ├── pages/
│   │   │   ├── CareBot.jsx       # Real-time health advisory companion
│   │   │   ├── Dashboard.jsx     # Clinical scorecard overview
│   │   │   ├── ProductSearch.jsx # Database and active AI scanning controller
│   │   │   ├── SignIn.jsx
│   │   │   └── SignUp.jsx
│   │   ├── App.jsx               # Application layouts & Routing
│   │   ├── index.css
│   │   └── main.jsx
│   ├── index.html
│   ├── postcss.config.js
│   ├── tailwind.config.js
│   ├── vite.config.js
│   └── package.json
└── README.md                     # Deployment manual
```

---

## 🛠️ Installation & Setup Manual

To run this application, make sure you have **NodeJS** (v18+) and a running instance of **MongoDB** (Local or MongoDB Atlas) installed.

### Step 1: Clone & Configure Environmental Keys

#### 💻 1. Configure Backend Variables
Navigate to the `Backend/` folder:
```bash
cd Backend
cp .env.example .env
```
Open the `.env` file and define your specific configurations:
```env
PORT=5000
MONGO_URI=mongodb://127.0.0.1:27017/caresure
JWT_SECRET=CaresureClinicalSecuritySecretKey2026
GEMINI_API_KEY=your_gemini_api_key_here
```

#### 💻 2. Install Dependencies
```bash
# In Backend folder:
npm install

# In Frontend folder:
cd ../Frontend
npm install
```

---

### Step 2: Seed Clinical Databases

The CareSure database includes exhaustive seed collections of real clinical products and chemical compounds (comprising safety warnings, comedogenic indices, and clean cosmetic benefits).

To populate the database prior to server initiation, run the database seeder inside the `Backend/` directory:
```bash
cd ../Backend
npm run seed
```
*(This triggers `node server.js --seed` to automatically connect, empty former rows, and insert the clinical mock data from `seedIngredients.json` and `seedProducts.json` into MongoDB).*

---

### Step 3: Run Developers Servers

We leverage concurrent servers for rapid full-stack execution:

#### 1. Launch Node Express Backend Services
From the `Backend/` directory:
```bash
npm run dev
```
The server bootstrapper will mount the routes and verify connection status:
`[MongoDB] CareSure Clinical Database successfully connected on port 5000`

#### 2. Launch Vite React Frontend Services
Open a separate terminal window and navigate to the `Frontend/` directory:
```bash
cd Frontend
npm run dev
```
Vite will serve the localized client panel, standardly at:
`http://localhost:5173`

---

## 🧪 Operational Use Cases

1. **Smart Scanner**: Go to the search tab and upload product cosmetics labels or pick a preset. The server passes the picture payload to Gemini, parses chemical formulas into JSON structures, displays diagnostic details, and renders comedogenic warning badges.
2. **Clinical Search**: Query over 100 Indian cosmetics, haircare, and pharmaceutical products to read ingredients and calculate personalized suitability match scores.
3. **CareBot Advisory**: Chat live with your private health specialist about retinoids, steroid thinning scales, or allergen triggers.
