// @desc    Discuss safe ingredient practices and skin advice with CareBot
// @route   POST /api/chat/message
exports.sendChatMessage = async (req, res) => {
  const { message, chatHistory, userProfile } = req.body;
  const apiKey = process.env.GEMINI_API_KEY;

  if (!message) {
    return res.status(400).json({ message: 'Message text is empty' });
  }

  // Pre-configured clinic guidelines focusing on skincare, haircare, and medicated compound safety
  const systemInstruction = `
    You are CareBot AI+, a high-fidelity clinical dermatology advisor and cosmetic safety assistant.
    Provide precise, scientific answers concerning ingredients, comedogenic ratings, dosages, and pregnancy safety.
    Always guide users concisely. If they ask about severe medical hazards (such as psoriasis flare-ups, heavy steroid-thinning scales, or cystic acne), provide educational facts and advise scheduling an in-person professional dermatologist checkup.
    
    Current User Profile Attributes (Check for safety matches):
    - Skin Type: ${userProfile?.skinType || 'Normal'}
    - Skin Concerns: ${userProfile?.skinConcerns?.join(', ') || 'None'}
    - Known Allergies: ${userProfile?.allergies?.join(', ') || 'None'}
    - Pregnancy Status: ${userProfile?.pregnancyStatus ? 'Pregnant (AVOID RETINODS, SALICYLIC ACID >2%, HYDROQUINONE)' : 'Not Pregnant'}
  `.trim();

  // If no Gemini API key is configured, perform standard smart heuristics matching on keywords
  if (!apiKey || apiKey === 'YOUR_GEMINI_API_KEY_HERE' || apiKey === 'null') {
    console.log('[CareBot Chat Fallback] No Gemini key found. Replying with smart clinical heuristics.');
    
    const msgLower = message.toLowerCase();
    let reply = "Hello! I am your CareBot assistant. It looks like my developer has not registered a live Gemini API key, but I can still offer clinical safety tips based on your profile.";

    if (msgLower.includes('retinol') || msgLower.includes('pregnancy') || msgLower.includes('pregnant')) {
      reply = "🚨 **Pregnancy Safety Alert**: Retinol, synthetic retinoids (like Tretinoin), and high-dose Salicylic Acid (>2%) must be completely avoided during pregnancy. Prefer gentle natural substitutes like **Bakuchiol**, Azelaic Acid, and standard low-concentration Niacinamide.";
    } else if (msgLower.includes('niacinamide') || msgLower.includes('breakout')) {
      reply = "✨ **Niacinamide (Vitamin B3) Analysis**: Niacinamide is excellent for barrier lipids and sebum regulation. It rarely triggers breakouts, but high concentrations (like 10-15%) may cause transient flushing if paired with low pH formulas. Try a 5% alternative.";
    } else if (msgLower.includes('dandruff') || msgLower.includes('ketoconazole') || msgLower.includes('scalp')) {
      reply = "🧼 **Scalp Therapy Guidance**: For oily dandruff scales, Ketoconazole 2% and Zinc Pyrithione are clinically proven antifungal agents. Use up to 3 times weekly, leave on scalp for 5 minutes, and combine with a moisturizing conditioner.";
    } else {
      reply = `Thanks for asking. Concerning "${message}", always check your product's clinical safety score. Avoid fragrances if you have Sensitive Skin, and make sure to wash thoroughly before application. Is there any specific active compound you want to decode?`;
    }

    return res.json({ response: reply });
  }

  try {
    // Collect previous message contents
    const contents = [];
    
    // Feed past history turns 
    if (chatHistory && Array.isArray(chatHistory)) {
      chatHistory.slice(-8).forEach(turn => {
        contents.push({
          role: turn.isUser ? 'user' : 'model',
          parts: [{ text: turn.message }]
        });
      });
    }

    // Add current turn
    contents.push({
      role: 'user',
      parts: [{ text: message }]
    });

    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${apiKey}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          contents,
          systemInstruction: {
            parts: [{ text: systemInstruction }]
          }
        }),
      }
    );

    const json = await response.json();
    if (!response.ok) {
      throw new Error(JSON.stringify(json));
    }

    const replyText = json.candidates[0].content.parts[0].text;
    res.json({ response: replyText });
  } catch (error) {
    console.error(`[CareBot AI Error] ${error.message}`);
    res.status(500).json({
      message: 'CareBot advisor was unable to parse this instruction. Please rephrase your query.',
      error: error.message,
    });
  }
};
