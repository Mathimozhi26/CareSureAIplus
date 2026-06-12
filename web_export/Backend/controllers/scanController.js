const Product = require('../models/Product');
const Ingredient = require('../models/Ingredient');

// @desc    Analyze uploaded or captured label image using Gemini
// @route   POST /api/scan/analyze
exports.analyzeLabelImage = async (req, res) => {
  const { imageBase64, mimeType } = req.body;
  const apiKey = process.env.GEMINI_API_KEY;

  if (!imageBase64) {
    return res.status(400).json({ message: 'No image payload or base64 data received' });
  }

  // Support local preset quick-scans instantly without requiring full AI calls
  if (imageBase64 === 'PRESET_ACNE_SPOT') {
    return res.json({
      productName: "Acne Relief Spot Gel",
      brand: "Anti-Acne Spot Cream",
      ingredients: ["Aqua", "Salicylic Acid 2%", "Tea Tree Oil", "Witch Hazel", "Glycerin", "Phenoxyethanol"],
      safetyScore: 82,
      safetyStatus: "Caution",
      explanation: "Formulated with 2% Salicylic Acid to exfoliate and clear pore-clogging debris, paired with soothing Tea Tree Oil. Witch Hazel skin-clarifies but can be drying for sensitive skin types."
    });
  }
  if (imageBase64 === 'PRESET_DANDRUFF') {
    return res.json({
      productName: "Therapy Dandruff Shampoo",
      brand: "Therapy Dandruff Wash",
      ingredients: ["Aqua", "Ketoconazole 2%", "Zinc Pyrithione", "Sodium Laureth Sulfate", "Cocamidopropyl Betaine"],
      safetyScore: 78,
      safetyStatus: "Caution",
      explanation: "Contains Ketoconazole and Zinc Pyrithione to actively combat dandruff causing fungal activity. May cause mild irritation or scalp dryness if used daily."
    });
  }
  if (imageBase64 === 'PRESET_CICA') {
    return res.json({
      productName: "Pure Calm Gel-Cream",
      brand: "Ultra Cica Calm-Gel",
      ingredients: ["Aqua", "Centella Asiatica Extract", "Niacinamide 10%", "Ceramide NP", "Glycerin", "Hyaluronic Acid"],
      safetyScore: 95,
      safetyStatus: "Safe",
      explanation: "An incredibly supportive cream featuring Centella (Cica) and Niacinamide. Ideal for rebuilding compromised skin barriers, reducing redness, and intense hydration."
    });
  }
  // Simulated Barcode inputs
  if (imageBase64 === 'BARCODE_PEARS') {
    return res.json({
      productName: "Pure & Gentle Soap",
      brand: "Pears",
      ingredients: ["Aqua", "Glycerin", "Lauric Acid", "Sorbitol", "Perfume", "Sodium Lauryl Sulfate"],
      safetyScore: 90,
      safetyStatus: "Safe",
      explanation: "A legendary glycerin soap bar known for gentle cleansing. Sorbitol and Glycerin naturally hydrate and lock moisture; contains mild fragrance."
    });
  }
  if (imageBase64 === 'BARCODE_HIMALAYA') {
    return res.json({
      productName: "Purifying Neem Face Wash",
      brand: "Himalaya",
      ingredients: ["Aqua", "Ammonium Lauryl Sulfate", "Melia Azadirachta Leaf Extract (Neem)", "Curcuma Longa Root Extract (Turmeric)", "Glycerin", "Phenoxyethanol"],
      safetyScore: 85,
      safetyStatus: "Safe",
      explanation: "Enriched with clinical grade Neem (antibacterial agent) and Turmeric to cleanse impurities, clear blackheads, and prevent acne recurrences without severe dryness."
    });
  }
  if (imageBase64 === 'BARCODE_VICCO') {
    return res.json({
      productName: "Turmeric Skin Cream",
      brand: "Vicco",
      ingredients: ["Aqua", "Turmeric Extract 16%", "Sandalwood Oil (Chandan Oil)", "Sorbitol", "Stearic Acid"],
      safetyScore: 94,
      safetyStatus: "Safe",
      explanation: "An ayurvedic skin cream with 16% Turmeric and Sandalwood Oil. Sandalwood cools and heals, while Turmeric acts as a natural antiseptic and skin rejuvenator."
    });
  }

  // If no Gemini API key, fall back to a high-fidelity mock scan representing "Dynamic Hydrating Cleanser"
  if (!apiKey || apiKey === 'YOUR_GEMINI_API_KEY_HERE' || apiKey === 'null') {
    console.log('[Scanner Fallback] No Gemini API Key. Triggering dynamic simulated analysis.');
    return res.json({
      productName: "Dynamic Hydrating Cleanser",
      brand: "PureHeal Glow",
      ingredients: ["Aqua", "Glycerin", "Niacinamide 10%", "Zinc PCA 1%", "Phenoxyethanol", "Sodium Hyaluronate"],
      safetyScore: 88,
      safetyStatus: "Safe",
      explanation: "This cleanser features high concentrations of Niacinamide (Vitamin B3) paired with Zinc PCA. This combo sebum-balances and reinforces raw dermal barriers without severe lipid stripping."
    });
  }

  try {
    const prompt = `
      You are an expert cosmetic and safety clinical ingredient analyzer.
      Analyze the attached image of a product label.
      1. Extract the likely Product Name and Brand. If not visible, guess or use "Unknown Product".
      2. Extract all visible ingredients as a clean list of comma-separated strings/items.
      3. Assess a Safety Score (0-100 AQ, where 100 is perfectly safe and non-toxic).
      4. Identify a Safety Status ("Safe", "Caution", or "Unsafe") based on common health alerts or user safety.
      5. Provide a 2-3 sentence clinical explanation of the major benefits and potential risks of these ingredients.

      Respond ONLY with a valid, clean JSON object matching this structure EXACTLY (do not include markdown formatting like \`\`\`json or \`\`\`, just the raw JSON text):
      {
        "productName": "extracted product name",
        "brand": "extracted brand name",
        "ingredients": ["ingredient1", "ingredient2", "ingredient3"],
        "safetyScore": 85,
        "safetyStatus": "Safe",
        "explanation": "clinical analysis explanation text here"
      }
    `;

    // Direct fetch rest call to Gemini 
    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${apiKey}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          contents: [
            {
              parts: [
                { text: prompt },
                {
                  inlineData: {
                    mimeType: mimeType || 'image/jpeg',
                    data: imageBase64,
                  },
                },
              ],
            },
          ],
        }),
      }
    );

    const json = await response.json();
    if (!response.ok) {
      throw new Error(JSON.stringify(json));
    }

    const rawText = json.candidates[0].content.parts[0].text;
    
    // Clean JSON response block
    let cleanText = rawText.trim();
    if (cleanText.startsWith('```json')) {
      cleanText = cleanText.substring(7, cleanText.length - 3).trim();
    } else if (cleanText.startsWith('```')) {
      cleanText = cleanText.substring(3, cleanText.length - 3).trim();
    }

    const result = JSON.parse(cleanText);
    res.json(result);
  } catch (error) {
    console.error(`[AI Scanner Error] ${error.message}`);
    res.status(500).json({
      message: 'Scanner analysis failed. Please verify label lighting or input resolution.',
      error: error.message,
    });
  }
};
