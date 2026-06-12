const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
  },
  password: {
    type: String,
    required: true,
  },
  skinType: {
    type: String,
    enum: ['Sensitive', 'Oily', 'Dry', 'Combination', 'Normal'],
    default: 'Normal',
  },
  skinConcerns: {
    type: [String],
    default: [], // e.g. "Acne", "Pigmentation", "Eczema"
  },
  allergies: {
    type: [String],
    default: [], // e.g. "Fragrance", "Sulfates", "Salicylic Acid"
  },
  pregnancyStatus: {
    type: Boolean,
    default: false,
  },
}, {
  timestamps: true,
});

// Pre-save hashing for password security
userSchema.pre('save', async function (next) {
  if (!this.isModified('password')) {
    next();
  }
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
});

// Method helper to compare secure input password
userSchema.methods.matchPassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

module.exports = mongoose.model('User', userSchema);
