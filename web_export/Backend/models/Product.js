const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  brand: {
    type: String,
    required: true,
  },
  category: {
    type: String,
    required: true,
    enum: ['Skincare', 'Haircare', 'Medicine'],
  },
  ingredients: {
    type: String, // Comma separated list of chemical compound keywords
    required: true,
  },
  benefits: {
    type: String,
    default: '',
  },
  warnings: {
    type: String,
    default: '',
  },
  safetyScore: {
    type: Number,
    required: true,
    min: 0,
    max: 100,
  },
  imageUrl: {
    type: String,
    default: '',
  },
  suitabilityTags: {
    type: String, // Comma separated tags, e.g. "Acne-prone, Oily Skin"
    default: '',
  }
}, {
  timestamps: true,
});

module.exports = mongoose.model('Product', productSchema);
