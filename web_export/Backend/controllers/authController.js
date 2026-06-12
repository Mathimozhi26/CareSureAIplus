const User = require('../models/User');
const jwt = require('jsonwebtoken');

// Helper to construct secure JWT token valid for 30 days
const generateToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET || 'CaresureClinicalSecuritySecretKey2026', {
    expiresIn: '30d',
  });
};

// @desc    Register a new clinical user profile
// @route   POST /api/auth/register
exports.registerUser = async (req, res) => {
  const { name, email, password, skinType, skinConcerns, allergies, pregnancyStatus } = req.body;

  try {
    const userExists = await User.findOne({ email });

    if (userExists) {
      return res.status(400).json({ message: 'User already exists with this clinical email' });
    }

    const user = await User.create({
      name,
      email,
      password,
      skinType: skinType || 'Normal',
      skinConcerns: skinConcerns || [],
      allergies: allergies || [],
      pregnancyStatus: pregnancyStatus || false,
    });

    if (user) {
      res.status(201).json({
        _id: user._id,
        name: user.name,
        email: user.email,
        skinType: user.skinType,
        skinConcerns: user.skinConcerns,
        allergies: user.allergies,
        pregnancyStatus: user.pregnancyStatus,
        token: generateToken(user._id),
      });
    } else {
      res.status(400).json({ message: 'Invalid clinical profile parameters' });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// @desc    Authenticate user & get token
// @route   POST /api/auth/login
exports.authUser = async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await User.findOne({ email });

    if (user && (await user.matchPassword(password))) {
      res.json({
        _id: user._id,
        name: user.name,
        email: user.email,
        skinType: user.skinType,
        skinConcerns: user.skinConcerns,
        allergies: user.allergies,
        pregnancyStatus: user.pregnancyStatus,
        token: generateToken(user._id),
      });
    } else {
      res.status(401).json({ message: 'Invalid medical email credentials or password' });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// @desc    Get user profile details
// @route   GET /api/auth/profile
exports.getUserProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user._id);

    if (user) {
      res.json({
        _id: user._id,
        name: user.name,
        email: user.email,
        skinType: user.skinType,
        skinConcerns: user.skinConcerns,
        allergies: user.allergies,
        pregnancyStatus: user.pregnancyStatus,
      });
    } else {
      res.status(404).json({ message: 'User profile not found' });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// @desc    Update user clinical factors
// @route   PUT /api/auth/profile
exports.updateUserProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user._id);

    if (user) {
      user.name = req.body.name || user.name;
      user.email = req.body.email || user.email;
      user.skinType = req.body.skinType || user.skinType;
      user.skinConcerns = req.body.skinConcerns || user.skinConcerns;
      user.allergies = req.body.allergies || user.allergies;
      user.pregnancyStatus = req.body.hasOwnProperty('pregnancyStatus') 
        ? req.body.pregnancyStatus 
        : user.pregnancyStatus;

      if (req.body.password) {
        user.password = req.body.password;
      }

      const updatedUser = await user.save();

      res.json({
        _id: updatedUser._id,
        name: updatedUser.name,
        email: updatedUser.email,
        skinType: updatedUser.skinType,
        skinConcerns: updatedUser.skinConcerns,
        allergies: updatedUser.allergies,
        pregnancyStatus: updatedUser.pregnancyStatus,
        token: generateToken(updatedUser._id),
      });
    } else {
      res.status(404).json({ message: 'User profile not found' });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
