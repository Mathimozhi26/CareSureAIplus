const mongoose = require('mongoose');

const ingredientSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true,
  },
  function: {
    type: String,
    default: 'Active compound',
  },
  benefits: {
    type: String,
    default: '',
  },
  sideEffects: {
    type: String,
    default: '',
  },
  riskLevel: {
    type: String,
    enum: ['Safe', 'Moderate', 'High Risk'],
    default: 'Safe',
  },
  scientificDescription: {
    type: String,
    default: '',
  },
  comedogenicRating: {
    type: Number,
    default: 0,
    min: 0,
    max: 5,
  },
  pregnancySafetyStatus: {
    type: String,
    enum: ['Safe', 'Avoid', 'Consult Doctor'],
    default: 'Safe',
  }
}, {
  timestamps: true,
});

module.exports = mongoose.model('Ingredient', ingredientSchema);
