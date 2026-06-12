import React, { useState, useEffect, useRef } from 'react';
import { 
  ShieldAlert, ScanLine, MessageSquare, Database, LogIn, UserPlus, 
  User, Activity, TrendingUp, Sparkles, LogOut, CheckCircle2, 
  AlertTriangle, Play, HelpCircle, Loader, Camera, Image, QrCode
} from 'lucide-react';

export default function App() {
  // Authentication & session state
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('caresure_user');
    return saved ? JSON.parse(saved) : null;
  });

  // Navigation states
  const [activeTab, setActiveTab] = useState('dashboard'); // 'dashboard', 'search', 'carebot', 'profile'
  const [authView, setAuthView] = useState('login'); // 'login' or 'signup'
  const [skinType, setSkinType] = useState(user?.skinType || 'Normal');
  const [allergies, setAllergies] = useState(user?.allergies || []);
  const [pregnancyStatus, setPregnancyStatus] = useState(user?.pregnancyStatus || false);

  // Search & Catalog states
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [catalogProducts, setCatalogProducts] = useState([
    {
      _id: '1',
      name: 'Dynamic Hydrating Cleanser',
      brand: 'PureHeal Glow',
      category: 'Skincare',
      ingredients: 'Aqua, Glycerin, Niacinamide 10%, Centella Asiatica Extract, Sodium Hyaluronate',
      benefits: 'Deeply hydrates skin structure, cleanses sebum, and reinforces dermal barrier shields.',
      warnings: 'Avoid getting directly into eyes. Mild tingling can happen for sensitive skin types.',
      safetyScore: 95,
      suitabilityTags: 'Dry Skin, Sensitive Skin, Oily Skin'
    },
    {
      _id: '2',
      name: 'Acne Relief Spot Gel',
      brand: 'Anti-Acne Spot Cream',
      category: 'Skincare',
      ingredients: 'Aqua, Salicylic Acid 2%, Glycerin, Centella Asiatica Extract, Phenoxyethanol',
      benefits: 'Dries active papules, clears pore blockages, and limits acne breakouts overnight.',
      warnings: 'Avoid during pregnancy. Wear sunblock during daytime. Mild flaking is expected.',
      safetyScore: 82,
      suitabilityTags: 'Acne-prone, Oily Skin'
    },
    {
      _id: '3',
      name: 'Therapy Dandruff Shampoo',
      brand: 'Therapy Dandruff Wash',
      category: 'Haircare',
      ingredients: 'Aqua, Ketoconazole 2%, Sodium Laureth Sulfate, Glycerin, Cocamidopropyl Betaine',
      benefits: 'Cleanses fungal scalp scale, stops active itch, and limits dandruff recurrence.',
      warnings: 'Do not swallow. Keep out of reach of infants. Use up to 3 times weekly maximum.',
      safetyScore: 78,
      suitabilityTags: 'Scalp Malassezia, Dandruff Only'
    },
    {
      _id: '4',
      name: 'Pure Calm Gel-Cream',
      brand: 'Ultra Cica Calm-Gel',
      category: 'Skincare',
      ingredients: 'Aqua, Centella Asiatica Extract, Niacinamide 10%, Glycerin, Hyaluronic Acid',
      benefits: 'Reduces active redness, hydrates, and repairs compromised skin layers.',
      warnings: 'None. Extremely safe for raw damaged barriers.',
      safetyScore: 98,
      suitabilityTags: 'Sensitive Skin, Rosacea, Laser Post-Care'
    }
  ]);

  // Selected details item
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedIngredient, setSelectedIngredient] = useState(null);

  // Scanner states
  const [scannerMode, setScannerMode] = useState('upload'); // 'upload' or 'barcode'
  const [isScannerLoading, setIsScannerLoading] = useState(false);
  const [scannedResult, setScannedResult] = useState(null);
  const [scannerError, setScannerError] = useState(null);
  const [showBarcodeDialog, setShowBarcodeDialog] = useState(false);

  // CareBot Chat states
  const [chatMessages, setChatMessages] = useState([
    { text: 'Hello! I am CareBot AI+, your personal health & formulation companion. Ask me any clinical questions regarding retinoids, scalp therapeutics, or comedogenic warnings.', isUser: false }
  ]);
  const [userInput, setUserInput] = useState('');
  const [isBotLoading, setIsBotLoading] = useState(false);

  // Core login handler
  const handleLogin = (e) => {
    e.preventDefault();
    const mockUser = {
      name: 'John Doe',
      email: e.target.email.value || 'john@example.com',
      skinType: 'Sensitive',
      allergies: ['Salicylic Acid'],
      pregnancyStatus: false,
    };
    setUser(mockUser);
    localStorage.setItem('caresure_user', JSON.stringify(mockUser));
    setSkinType(mockUser.skinType);
    setAllergies(mockUser.allergies);
    setPregnancyStatus(mockUser.pregnancyStatus);
  };

  // Core registration handler
  const handleSignUp = (e) => {
    e.preventDefault();
    const mockUser = {
      name: e.target.username.value || 'New Clinical Evaluator',
      email: e.target.email.value || 'new@example.com',
      skinType: skinType,
      allergies: allergies,
      pregnancyStatus: pregnancyStatus,
    };
    setUser(mockUser);
    localStorage.setItem('caresure_user', JSON.stringify(mockUser));
  };

  // Logout
  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('caresure_user');
    setActiveTab('dashboard');
  };

  // Save profile settings
  const saveProfile = (e) => {
    e.preventDefault();
    const updated = { ...user, skinType, allergies, pregnancyStatus };
    setUser(updated);
    localStorage.setItem('caresure_user', JSON.stringify(updated));
    alert('User clinical safety metrics saved successfully!');
  };

  // Toggle allergies list
  const toggleAllergy = (allergy) => {
    if (allergies.includes(allergy)) {
      setAllergies(allergies.filter(item => item !== allergy));
    } else {
      setAllergies([...allergies, allergy]);
    }
  };

  // Clinical calculation: safety and suitability
  const calculatePersonalScore = (product) => {
    let suitScore = 100;
    let reasons = [];
    let alerts = [];

    // Check allergy triggers
    allergies.forEach(allergy => {
      if (product.ingredients.toLowerCase().includes(allergy.toLowerCase())) {
        suitScore -= 45;
        reasons.push(`Contains identified allergens: ${allergy}`);
        alerts.push(`ALLERGY WARNING: ${allergy} detected!`);
      }
    });

    // Pregnancy risk overrides
    if (pregnancyStatus) {
      const pregnancyBannedKeywords = ['retinol', 'salicylic acid', 'ketoconazole'];
      pregnancyBannedKeywords.forEach(keyword => {
        if (product.ingredients.toLowerCase().includes(keyword)) {
          suitScore -= 60;
          reasons.push(`Contains high-risk pregnancy compound: ${keyword}`);
          alerts.push(`PREGNANCY ALERT: ${keyword} found. Retinoids & BHAs must be avoided.`);
        }
      });
    }

    // Skin suitability
    if (skinType === 'Sensitive') {
      if (product.ingredients.toLowerCase().includes('laureth sulfate') || product.ingredients.toLowerCase().includes('perfume')) {
        suitScore -= 20;
        reasons.push('Contains drying sulfates or fragrances sensitive for raw skin barriers.');
      }
    }
    if (skinType === 'Oily' && product.suitabilityTags.toLowerCase().includes('oily')) {
      suitScore += 10;
    }

    const finalSuitability = Math.max(0, Math.min(100, suitScore));
    let status = 'High Suitability';
    if (finalSuitability < 40) status = 'Dangerous / Avoid';
    else if (finalSuitability < 75) status = 'Moderate Risk';

    return {
      safetyScore: product.safetyScore,
      suitabilityScore: finalSuitability,
      severityLevel: status,
      analysisSummary: reasons.length > 0 
        ? reasons.join(' ') 
        : `Formulation matches your ${skinType} profile perfectly with safe non-toxic thresholds.`,
      alerts: alerts
    };
  };

  // AI Multimodal analysis simulation
  const handleAIAnalysis = (keyword) => {
    setIsScannerLoading(true);
    setScannedResult(null);
    setScannerError(null);

    // Simulated latency for medical formula extraction
    setTimeout(() => {
      let mockProduct = null;
      if (keyword === 'PRESET_ACNE_SPOT') {
        mockProduct = {
          productName: "Acne Relief Spot Gel",
          brand: "Anti-Acne Spot Cream",
          ingredients: ["Aqua", "Salicylic Acid 2%", "Tea Tree Oil", "Witch Hazel", "Glycerin", "Phenoxyethanol"],
          safetyScore: 82,
          safetyStatus: "Caution",
          explanation: "Formulated with 2% Salicylic Acid to exfoliate and clear pore-clogging debris, paired with soothing Tea Tree Oil. Witch Hazel skin-clarifies but can be drying for sensitive skin types."
        };
      } else if (keyword === 'PRESET_DANDRUFF') {
        mockProduct = {
          productName: "Therapy Dandruff Shampoo",
          brand: "Therapy Dandruff Wash",
          ingredients: ["Aqua", "Ketoconazole 2%", "Zinc Pyrithione", "Sodium Laureth Sulfate", "Cocamidopropyl Betaine"],
          safetyScore: 78,
          safetyStatus: "Caution",
          explanation: "Contains Ketoconazole and Zinc Pyrithione to actively combat dandruff causing fungal activity. May cause mild irritation or scalp dryness if used daily."
        };
      } else if (keyword === 'PRESET_CICA') {
        mockProduct = {
          productName: "Pure Calm Gel-Cream",
          brand: "Ultra Cica Calm-Gel",
          ingredients: ["Aqua", "Centella Asiatica Extract", "Niacinamide 10%", "Ceramide NP", "Glycerin", "Hyaluronic Acid"],
          safetyScore: 95,
          safetyStatus: "Safe",
          explanation: "An incredibly supportive cream featuring Centella (Cica) and Niacinamide. Ideal for rebuilding compromised skin barriers, reducing redness, and intense hydration."
        };
      } else if (keyword === 'BARCODE_PEARS') {
        mockProduct = {
          productName: "Pure & Gentle Soap",
          brand: "Pears",
          ingredients: ["Aqua", "Glycerin", "Lauric Acid", "Sorbitol", "Perfume", "Sodium Lauryl Sulfate"],
          safetyScore: 90,
          safetyStatus: "Safe",
          explanation: "A legendary glycerin soap bar known for gentle cleansing. Sorbitol and Glycerin naturally hydrate and lock moisture; contains mild fragrance."
        };
      } else if (keyword === 'BARCODE_HIMALAYA') {
        mockProduct = {
          productName: "Purifying Neem Face Wash",
          brand: "Himalaya",
          ingredients: ["Aqua", "Ammonium Lauryl Sulfate", "Melia Azadirachta Leaf Extract (Neem)", "Curcuma Longa Root Extract (Turmeric)", "Glycerin", "Phenoxyethanol"],
          safetyScore: 85,
          safetyStatus: "Safe",
          explanation: "Enriched with clinical grade Neem (antibacterial agent) and Turmeric to cleanse impurities, clear blackheads, and prevent acne recurrences without severe dryness."
        };
      } else if (keyword === 'BARCODE_VICCO') {
        mockProduct = {
          productName: "Turmeric Skin Cream",
          brand: "Vicco",
          ingredients: ["Aqua", "Turmeric Extract 16%", "Sandalwood Oil (Chandan Oil)", "Sorbitol", "Stearic Acid"],
          safetyScore: 94,
          safetyStatus: "Safe",
          explanation: "An ayurvedic skin cream with 16% Turmeric and Sandalwood Oil. Sandalwood cools and heals, while Turmeric acts as a natural antiseptic and skin rejuvenator."
        };
      } else {
        // High fidelity generic upload analyzer
        mockProduct = {
          productName: "Dynamic Hydrating Cleanser",
          brand: "PureHeal Glow",
          ingredients: ["Aqua", "Glycerin", "Niacinamide 10%", "Zinc PCA 1%", "Phenoxyethanol", "Sodium Hyaluronate"],
          safetyScore: 88,
          safetyStatus: "Safe",
          explanation: "This cleanser features high concentrations of Niacinamide (Vitamin B3) paired with Zinc PCA. This combo sebum-balances and reinforces raw dermal barriers without severe lipid stripping."
        };
      }

      setScannedResult(mockProduct);
      setIsScannerLoading(false);
    }, 2000);
  };

  // Bot response generator
  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!userInput.trim()) return;

    const currentMsg = userInput;
    const history = [...chatMessages, { text: currentMsg, isUser: true }];
    setChatMessages(history);
    setUserInput('');
    setIsBotLoading(true);

    setTimeout(() => {
      const msgLower = currentMsg.toLowerCase();
      let response = "I have evaluated your chemistry query. For maximum skin barrier health, always verify that your formulation doesn't exceed 10% on active concentrations and apply SPF daily.";

      if (msgLower.includes('retinol') || msgLower.includes('pregnancy') || msgLower.includes('pregnant')) {
        response = "🚨 **Clinical Alert**: Retinoids are strictly contraindicated during pregnancy due to systemic absorption hazards. For matching anti-aging results, consult Bakuchiol (a natural botanical) or Azelaic Acid.";
      } else if (msgLower.includes('dandruff') || msgLower.includes('scalp') || msgLower.includes('ketoconazole')) {
        response = "🧼 **Anti-Dandruff Protocol**: Ketoconazole 2% represents the clinical standard for combating dandruff. Leave active foam on hair scale for 4-5 minutes before washing fully. Pair with a deep moisturising follicle treatment.";
      } else if (msgLower.includes('allergy') || msgLower.includes('salicylic')) {
        response = "🔬 **Salicylic Acid (BHA)**: Formulated up to 2%, this oil-soluble BHA dissolves cellular plugs effectively. However, dry skin types or users with active aspirin allergies should avoid high exposures.";
      }

      setChatMessages([...history, { text: response, isUser: false }]);
      setIsBotLoading(false);
    }, 1200);
  };

  // Search filter core
  const filteredProducts = catalogProducts.filter(p => {
    const matchesSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          p.brand.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          p.ingredients.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCat = selectedCategory === 'All' || p.category === selectedCategory;
    return matchesSearch && matchesCat;
  });

  // Offline ingredient safety details sheet matching
  const fetchIngredientDetails = (name) => {
    const database = {
      "niacinamide 10%": {
        name: "Niacinamide 10%",
        function: "Barrier Support & Sebum Regulation",
        benefits: "Deeply strengthens lipid barrier, speeds up cellular turn-over, balances overactive sebum production, and brightens residual acne spot pigmentation.",
        sideEffects: "Very safe; extremely high doses may trigger temporary skin flushes.",
        riskLevel: "Safe",
        scientificDescription: "Niacinamide (Vitamin B3) is a water-soluble co-enzyme precursor. It actively triggers synthesis of ceramides and keratin to lock moisture.",
        comedogenicRating: 0,
        pregnancySafetyStatus: "Safe"
      },
      "salicylic acid 2%": {
        name: "Salicylic Acid 2%",
        function: "Beta-Hydroxy Acid (BHA) Pore Exfoliant",
        benefits: "Dissolves cellular cohesion inside the pores, breaks oil glands down, prevents cystic acne lesions, and clarifies active pustules.",
        sideEffects: "Mild localized peeling, drying, or irritation if used alongside drying alcohol vehicles.",
        riskLevel: "Moderate",
        scientificDescription: "Salicylic Acid is a highly lipophilic clinical BHA that penetrates deep straight into human lipid pores.",
        comedogenicRating: 0,
        pregnancySafetyStatus: "Avoid"
      },
      "ketoconazole 2%": {
        name: "Ketoconazole 2%",
        function: "Antifungal Clinical Active",
        benefits: "Inhibits fungal cell ergosterol synthesis, rapidly resolving Malassezia scalps, reducing oily dandruff flakes, and calming itch.",
        sideEffects: "Mild burning scaling sensation or local hair structural change.",
        riskLevel: "Moderate",
        scientificDescription: "Ketoconazole is a synthetic imidazole compound utilized to stop dermatophyte fungal cells from replicating.",
        comedogenicRating: 0,
        pregnancySafetyStatus: "Consult Doctor"
      }
    };

    const clean = name.trim().toLowerCase();
    const match = database[clean] || {
      name: name,
      function: 'Cosmetic Compound / Botanical Active',
      benefits: 'Serves as an excipient, emulsion stabilizer, or specialized clinical active within standard formulas.',
      sideEffects: 'Generally non-irritating; skin tolerance checks recommended.',
      riskLevel: 'Safe',
      scientificDescription: `${name} is an emulsifying or active compound commonly verified across global pharmacopoeia.`,
      comedogenicRating: 0,
      pregnancySafetyStatus: 'Safe'
    };

    setSelectedIngredient(match);
  };

  // Convert Scanned result into Catalog product
  const importScannedProduct = (scanned) => {
    const id = (catalogProducts.length + 1).toString();
    const converted = {
      _id: id,
      name: scanned.productName,
      brand: scanned.brand,
      category: 'Skincare',
      ingredients: scanned.ingredients.join(', '),
      benefits: scanned.explanation,
      warnings: 'Simulated scanning data, perform spot evaluation before full clinical application.',
      safetyScore: scanned.safetyScore,
      suitabilityTags: 'Dry Skin, Sensitive Skin, Oily Skin'
    };
    setCatalogProducts([converted, ...catalogProducts]);
    setSelectedProduct(converted);
    setScannedResult(null);
    setActiveTab('search');
  };

  // Authentication screens
  if (!user) {
    return (
      <div className="flex-1 flex flex-col justify-center items-center px-6 py-12 bg-darkBg text-textWhite font-sans min-h-screen">
        <div className="w-full max-w-md bg-darkCard border border-slate-800 p-8 rounded-2xl shadow-xl flex flex-col gap-6">
          <div className="flex flex-col items-center text-center">
            <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 rounded-2xl mb-3">
              <ShieldAlert size={36} />
            </div>
            <h1 className="text-2xl font-extrabold tracking-tight">CareSure AI+</h1>
            <p className="text-sm text-textGray">MERN Formulation Intelligence Core</p>
          </div>

          {authView === 'login' ? (
            <form onSubmit={handleLogin} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-bold uppercase text-textGray">Medical Email ID</label>
                <input required name="email" type="email" placeholder="doctor@caresure.com" className="w-full bg-[#0F172A] border border-slate-800 px-4 py-2.5 rounded-xl text-sm text-textWhite focus:border-accentMint outline-none" />
              </div>
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-bold uppercase text-textGray">Security Password</label>
                <input required name="password" type="password" placeholder="••••••••" className="w-full bg-[#0F172A] border border-slate-800 px-4 py-2.5 rounded-xl text-sm text-textWhite focus:border-accentMint outline-none" />
              </div>
              <button type="submit" className="w-full h-11 bg-accentMint hover:bg-accentEmerald text-textWhite font-bold rounded-xl text-sm flex items-center justify-center gap-2 mt-2 transition-all">
                <LogIn size={16} /> Authenticate Session
              </button>
              <p className="text-xs text-center text-textGray mt-2">
                New to the diagnostic matrix?{' '}
                <button type="button" onClick={() => setAuthView('signup')} className="font-semibold text-accentMint hover:underline">Register Profile</button>
              </p>
            </form>
          ) : (
            <form onSubmit={handleSignUp} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-bold uppercase text-textGray">Evaluator Username</label>
                <input required name="username" type="text" placeholder="Dr. John Doe" className="w-full bg-[#0F172A] border border-slate-800 px-4 py-2.5 rounded-xl text-sm text-textWhite focus:border-accentMint outline-none" />
              </div>
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-bold uppercase text-textGray">Clinical Email ID</label>
                <input required name="email" type="email" placeholder="john@example.com" className="w-full bg-[#0F172A] border border-slate-800 px-4 py-2.5 rounded-xl text-sm text-textWhite focus:border-accentMint outline-none" />
              </div>
              
              <div className="border-t border-slate-800 my-2 pt-4 flex flex-col gap-3">
                <h4 className="text-xs font-bold uppercase text-emerald-400">Clinical Directives</h4>
                
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-textGray">Clinical Dermatological Type</label>
                  <select value={skinType} onChange={(e) => setSkinType(e.target.value)} className="w-full bg-[#0F172A] border border-slate-800 px-3 py-2 rounded-xl text-sm text-textWhite outline-none">
                    <option>Normal</option>
                    <option>Sensitive</option>
                    <option>Oily</option>
                    <option>Dry</option>
                    <option>Combination</option>
                  </select>
                </div>

                <div className="flex flex-col gap-2">
                  <label className="text-xs text-textGray">Identify Sensitive Compounds Allergies:</label>
                  <div className="grid grid-cols-2 gap-2">
                    {['Salicylic Acid', 'Fragrance', 'Sulfates', 'Ketoconazole'].map(item => (
                      <button key={item} type="button" onClick={() => toggleAllergy(item)} className={`border px-3 py-1.5 rounded-xl text-xs font-medium text-left truncate ${allergies.includes(item) ? 'bg-red-500/10 border-red-500/40 text-red-400' : 'bg-slate-900 border-slate-800 hover:border-slate-700'}`}>
                        {item}
                      </button>
                    ))}
                  </div>
                </div>

                <label className="flex items-center gap-2 cursor-pointer select-none text-xs text-textGray mt-1">
                  <input type="checkbox" checked={pregnancyStatus} onChange={(e) => setPregnancyStatus(e.target.checked)} className="rounded border-slate-800 bg-[#0F172A] text-accentMint focus:ring-accentMint" />
                  Apply Pregnancy Safety Matrix Filtering
                </label>
              </div>

              <button type="submit" className="w-full h-11 bg-accentMint hover:bg-accentEmerald text-textWhite font-bold rounded-xl text-sm flex items-center justify-center gap-2 mt-2 transition-all">
                <UserPlus size={16} /> Initialise Account Keys
              </button>
              <p className="text-xs text-center text-textGray mt-2">
                Already registered?{' '}
                <button type="button" onClick={() => setAuthView('login')} className="font-semibold text-accentMint hover:underline">Log In</button>
              </p>
            </form>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col md:flex-row bg-[#020617] text-textWhite min-h-screen font-sans antialiased">
      {/* Dynamic desktop navigation */}
      <aside className="w-full md:w-64 bg-darkCard border-b md:border-b-0 md:border-r border-slate-800/80 p-6 flex flex-col justify-between">
        <div className="flex flex-col gap-8">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 rounded-xl">
              <ShieldAlert size={24} />
            </div>
            <div>
              <h2 className="text-large font-black uppercase text-textWhite">CareSure AI+</h2>
              <p className="text-xs text-emerald-400 font-semibold uppercase tracking-wider">Clinical Suite</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2">
            {[
              { id: 'dashboard', label: 'Safety Overview', icon: Activity },
              { id: 'search', label: 'Clinics Library', icon: Database },
              { id: 'carebot', label: 'Bot Consultation', icon: MessageSquare },
              { id: 'profile', label: 'Clinical Factors', icon: User },
            ].map(tab => {
              const IconComp = tab.icon;
              return (
                <button key={tab.id} onClick={() => { setActiveTab(tab.id); setSelectedProduct(null); }} className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold transition-all ${activeTab === tab.id ? 'bg-accentMint text-textWhite shadow-lg shadow-emerald-950/20' : 'text-textGray hover:bg-[#1E293B]/40 hover:text-textWhite'}`}>
                  <IconComp size={18} /> {tab.label}
                </button>
              )
            })}
          </nav>
        </div>

        <div className="flex flex-col gap-4 border-t border-slate-800/80 pt-4 mt-6 md:mt-0">
          <div className="flex items-center gap-2.5">
            <div className="h-9 w-9 bg-accentMint/10 border border-accentMint/20 text-accentMint rounded-full flex items-center justify-center font-bold text-xs uppercase">
              {user.name ? user.name.slice(0, 2) : 'US'}
            </div>
            <div className="overflow-hidden">
              <p className="text-xs font-bold text-textWhite truncate">{user.name}</p>
              <p className="text-[10px] text-textGray uppercase truncate">{skinType} Skin</p>
            </div>
          </div>
          <button onClick={handleLogout} className="flex items-center gap-2 justify-center w-full py-2 border border-slate-800/80 hover:bg-clinicalRed/10 text-textGray hover:text-clinicalRed text-xs font-bold rounded-xl transition-all">
            <LogOut size={14} /> Exit Lab Matrix
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col overflow-y-auto px-6 py-8 md:px-10 lg:px-12">
        {activeTab === 'dashboard' && (
          <div className="flex flex-col gap-8 max-w-5xl">
            <div className="flex flex-col gap-2">
              <h1 className="text-3xl font-extrabold tracking-tight">Safeguard Scoring Board</h1>
              <p className="text-sm text-textGray">Real-time skincare database monitoring & direct formulation checks.</p>
            </div>

            {/* Quick Metrics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-darkCard border border-slate-800 p-5 rounded-2xl flex items-center gap-4">
                <div className="p-3.5 bg-emerald-500/10 text-emerald-400 rounded-xl">
                  <Activity size={24} />
                </div>
                <div>
                  <h4 className="text-xs uppercase text-textGray font-bold">Personal Score</h4>
                  <p className="text-2xl font-black text-textWhite">100% Secure</p>
                </div>
              </div>
              <div className="bg-darkCard border border-slate-800 p-5 rounded-2xl flex items-center gap-4">
                <div className="p-3.5 bg-sky-500/10 text-sky-400 rounded-xl">
                  <Database size={24} />
                </div>
                <div>
                  <h4 className="text-xs uppercase text-textGray font-bold">Total Catalog Items</h4>
                  <p className="text-2xl font-black text-textWhite">{catalogProducts.length} Seeding Items</p>
                </div>
              </div>
              <div className="bg-darkCard border border-slate-800 p-5 rounded-2xl flex items-center gap-4">
                <div className="p-3.5 bg-purple-500/10 text-purple-400 rounded-xl">
                  <ScanLine size={24} />
                </div>
                <div>
                  <h4 className="text-xs uppercase text-textGray font-bold">Skin Directives</h4>
                  <p className="text-md font-bold text-textWhite truncate">{skinType} Classify</p>
                </div>
              </div>
            </div>

            {/* AI MULTIMODAL INGREDIENT ANALYSER WIDGET */}
            <div className="flex flex-col gap-6 bg-darkCard border border-slate-800 p-6 rounded-3xl">
              <div>
                <h3 className="text-xl font-bold text-textWhite">AI Multimodal Ingredient Analyser</h3>
                <p className="text-sm text-textGray mt-1">Upload formula label files, capture photos or simulate laser barcode matching to decode active hazards instantly.</p>
              </div>

              {!scannedResult && !isScannerLoading ? (
                <div className="flex flex-col gap-6">
                  {/* Real scan triggering box */}
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <button onClick={() => handleAIAnalysis('GENERIC')} className="flex flex-col items-center justify-center gap-2 border border-dashed border-slate-800 hover:border-accentMint hover:bg-accentMint/5 p-6 rounded-2xl transition-all">
                      <Camera size={28} className="text-accentMint" />
                      <span className="text-xs font-semibold text-textWhite">Capture Live Photo</span>
                    </button>
                    <button onClick={() => handleAIAnalysis('GENERIC')} className="flex flex-col items-center justify-center gap-2 border border-dashed border-slate-800 hover:border-accentMint hover:bg-accentMint/5 p-6 rounded-2xl transition-all">
                      <Image size={28} className="text-accentMint" />
                      <span className="text-xs font-semibold text-textWhite">Upload Image File</span>
                    </button>
                    <button onClick={() => setShowBarcodeDialog(true)} className="flex flex-col items-center justify-center gap-2 border border-dashed border-slate-800 hover:border-accentMint hover:bg-accentMint/5 p-6 rounded-2xl transition-all">
                      <QrCode size={28} className="text-accentMint" />
                      <span className="text-xs font-semibold text-textWhite">Scan Barcode Sensor</span>
                    </button>
                  </div>

                  {/* Simulator Presets */}
                  <div className="border-t border-slate-800/80 pt-4 flex flex-col gap-4">
                    <h5 className="text-sm font-bold text-textWhite">Quick-Scan Presets :</h5>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      {[
                        { title: 'Anti-Acne Spot Cream', key: 'PRESET_ACNE_SPOT', desc: 'Salicylic Acid 2% Exfoliant' },
                        { title: 'Therapy Dandruff Wash', key: 'PRESET_DANDRUFF', desc: 'Ketoconazole Scalp Active' },
                        { title: 'Ultra Cica Calm-Gel', key: 'PRESET_CICA', desc: 'Cica Barrier Gel' }
                      ].map(item => (
                        <div key={item.key} onClick={() => handleAIAnalysis(item.key)} className="bg-[#0F172A] border border-slate-800 hover:border-slate-700 p-4 rounded-xl cursor-pointer flex flex-col justify-between transition-all">
                          <div>
                            <p className="text-xs font-bold text-textWhite">{item.title}</p>
                            <p className="text-[10px] text-textGray mt-0.5">{item.desc}</p>
                          </div>
                          <span className="text-[11px] font-semibold text-accentMint flex items-center gap-1 mt-3">Trigger Preview <Play size={10} /></span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              ) : isScannerLoading ? (
                <div className="h-44 flex flex-col justify-center items-center gap-3 bg-[#0F172A] border border-slate-800 rounded-2xl">
                  <Loader size={36} className="text-accentMint animate-spin" />
                  <div className="text-center">
                    <p className="text-sm font-bold text-textWhite">CareBot AI chemical formula decoder is active...</p>
                    <p className="text-xs text-textGray mt-0.5">Calculating allergen match matrices and score thresholds</p>
                  </div>
                </div>
              ) : (
                <div className="flex flex-col gap-5 bg-[#0F172A] border border-emerald-500/20 p-5 rounded-2xl">
                  <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                    <div>
                      <p className="text-xs text-emerald-400 font-bold uppercase tracking-wider">AI Analysis Complete</p>
                      <h4 className="text-lg font-bold text-textWhite mt-0.5">{scannedResult.productName}</h4>
                      <p className="text-xs text-textGray">Brand: {scannedResult.brand}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-xs text-textGray uppercase">Clinical Score</p>
                      <p className="text-xl font-extrabold text-accentMint">{scannedResult.safetyScore}% Safe</p>
                    </div>
                  </div>

                  <div>
                    <p className="text-xs font-bold text-textWhite uppercase mb-1">Decoded Formulas:</p>
                    <div className="flex flex-wrap gap-1.5">
                      {scannedResult.ingredients.map(ing => (
                        <span key={ing} onClick={() => fetchIngredientDetails(ing)} className="bg-emerald-500/10 border border-emerald-500/20 px-2.5 py-1 text-xs text-emerald-400 rounded-full hover:bg-emerald-500/20 cursor-pointer">
                          {ing}
                        </span>
                      ))}
                    </div>
                  </div>

                  <p className="text-sm text-textGray italic leading-relaxed">"{scannedResult.explanation}"</p>

                  <div className="flex gap-3 mt-2">
                    <button onClick={() => importScannedProduct(scannedResult)} className="px-4 py-2 bg-accentMint hover:bg-accentEmerald text-textWhite text-xs font-bold rounded-xl transition-all">
                      Import to Cabinet / Database
                    </button>
                    <button onClick={() => setScannedResult(null)} className="px-4 py-2 border border-slate-800 hover:bg-slate-900 text-textGray text-xs font-bold rounded-xl transition-all">
                      Discard Analysis
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Quick Warning Matrix Details */}
            {pregnancyStatus && (
              <div className="bg-red-500/5 border border-red-500/20 p-5 rounded-2xl flex items-center gap-4">
                <AlertTriangle className="text-red-400 shrink-0" size={32} />
                <div>
                  <h4 className="text-sm font-bold text-red-400">Pregnancy Filtering Shield is Active</h4>
                  <p className="text-xs text-textGray mt-0.5">Platform is automatically flagging formulation matches containing Retinoids (cell division compounds), Salicylic Acids above 2%, or chemical exfoliators.</p>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'search' && (
          <div className="flex flex-row gap-8 flex-1 min-h-[400px]">
            {/* Catalog list Panel */}
            <div className="flex-1 flex flex-col gap-6">
              <div className="flex flex-col gap-1.5">
                <h1 className="text-2xl font-extrabold tracking-tight">Clinics Formulation Database</h1>
                <p className="text-xs text-textGray">Verify active matrices against your profile offline.</p>
              </div>

              {/* Filters */}
              <div className="flex flex-col gap-3 md:flex-row md:items-center">
                <input value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} type="text" placeholder="Search 100+ diagnostic formulations..." className="flex-1 bg-darkCard border border-slate-800 px-4 py-2.5 rounded-xl text-sm outline-none focus:border-accentMint" />
                <div className="flex gap-1.5">
                  {['All', 'Skincare', 'Haircare', 'Medicine'].map(cat => (
                    <button key={cat} onClick={() => setSelectedCategory(cat)} className={`px-4 py-2 text-xs font-bold rounded-xl border transition-all ${selectedCategory === cat ? 'bg-accentMint border-accentMint text-textWhite' : 'bg-darkCard border-slate-800 hover:border-slate-700 text-textGray'}`}>
                      {cat}
                    </button>
                  ))}
                </div>
              </div>

              {/* Catalog result loop */}
              <div className="flex flex-col gap-3">
                {filteredProducts.map(p => {
                  const personal = calculatePersonalScore(p);
                  return (
                    <div key={p._id} onClick={() => setSelectedProduct(p)} className={`bg-darkCard border hover:border-slate-700 p-4 rounded-2xl cursor-pointer flex justify-between items-center transition-all ${selectedProduct?._id === p._id ? 'border-accentMint shadow-lg' : 'border-slate-800'}`}>
                      <div>
                        <h4 className="text-sm font-bold text-textWhite">{p.name}</h4>
                        <p className="text-xs text-textGray mt-0.5">Brand: {p.brand} | {p.category}</p>
                        <p className="text-[11px] text-accentMint font-semibold mt-1">{p.suitabilityTags}</p>
                      </div>
                      <div className="text-right">
                        <span className="text-[10px] text-textGray uppercase block">Suitability Match</span>
                        <span className={`text-md font-extrabold ${personal.suitabilityScore < 45 ? 'text-clinicalRed' : 'text-accentMint'}`}>{personal.suitabilityScore}%</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Selected detail sheet drawer inside search */}
            {selectedProduct && (
              <div className="w-96 bg-darkCard border border-slate-800 p-6 rounded-2xl hover:border-slate-700 flex flex-col gap-5 select-none self-start shrink-0">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-bold text-textWhite">{selectedProduct.name}</h3>
                    <p className="text-xs text-textGray">Category: {selectedProduct.category}</p>
                  </div>
                  <button onClick={() => setSelectedProduct(null)} className="text-textGray hover:text-textWhite font-bold text-xs">Close</button>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="p-3 bg-[#0F172A] border border-slate-800 rounded-xl text-center">
                    <p className="text-[10px] uppercase text-textGray font-bold">Safety Index</p>
                    <p className="text-lg font-black text-accentMint">{selectedProduct.safetyScore}%</p>
                  </div>
                  <div className="p-3 bg-[#0F172A] border border-slate-800 rounded-xl text-center">
                    <p className="text-[10px] uppercase text-textGray font-bold">Personal Match</p>
                    <p className={`text-lg font-black ${calculatePersonalScore(selectedProduct).suitabilityScore < 40 ? 'text-clinicalRed' : 'text-accentMint'}`}>{calculatePersonalScore(selectedProduct).suitabilityScore}%</p>
                  </div>
                </div>

                {calculatePersonalScore(selectedProduct).alerts.map((al, idx) => (
                  <div key={idx} className="bg-red-500/10 border border-red-500/30 text-red-400 p-3 rounded-xl text-xs flex gap-2">
                    <AlertTriangle className="shrink-0 text-red-400" size={14} />
                    <span>{al}</span>
                  </div>
                ))}

                <div>
                  <h4 className="text-xs font-bold uppercase text-textWhite mb-1">Active Ingredient Compounds:</h4>
                  <div className="flex flex-wrap gap-1.5">
                    {selectedProduct.ingredients.split(',').map(ing => (
                      <button key={ing} onClick={() => fetchIngredientDetails(ing)} className="bg-slate-900 hover:bg-slate-800 border border-slate-800 px-2.5 py-1 rounded-full text-xs text-emerald-400 select-none">
                        {ing.trim()}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="flex flex-col gap-1.5 border-t border-slate-800 pt-3">
                  <h5 className="text-xs font-bold uppercase text-textWhite">Clinical Benefits:</h5>
                  <p className="text-xs text-textGray leading-relaxed">{selectedProduct.benefits}</p>
                </div>

                <div className="flex flex-col gap-1.5 border-t border-slate-800 pt-3">
                  <h5 className="text-xs font-bold uppercase text-clinicalRed">Warnings:</h5>
                  <p className="text-xs text-textGray leading-relaxed">{selectedProduct.warnings}</p>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'carebot' && (
          <div className="flex flex-col gap-6 max-w-4xl flex-1 justify-between">
            <div className="flex flex-col gap-1.5">
              <h1 className="text-2xl font-extrabold tracking-tight">CareBot Health Advisor 🤖</h1>
              <p className="text-xs text-textGray">Consult active chemical formulas or pregnancy-safe ratios live.</p>
            </div>

            {/* Chat list */}
            <div className="flex-1 bg-darkCard border border-slate-800 p-5 rounded-2xl flex flex-col gap-3 min-h-[350px] overflow-y-auto">
              {chatMessages.map((msg, i) => (
                <div key={i} className={`flex ${msg.isUser ? 'justify-end' : 'justify-start'}`}>
                  <div className={`px-4 py-2.5 rounded-xl text-sm leading-relaxed max-w-md ${msg.isUser ? 'bg-accentMint text-textWhite' : 'bg-slate-900 border border-slate-800 text-textWhite'}`}>
                    {msg.text}
                  </div>
                </div>
              ))}
              {isBotLoading && <span className="text-xs text-textGray animate-pulse">typing...</span>}
            </div>

            {/* Input form */}
            <form onSubmit={handleSendMessage} className="flex gap-3">
              <input value={userInput} onChange={(e) => setUserInput(e.target.value)} type="text" placeholder="Inquire about Retinol, Dandruff, Niacinamide breakouts..." className="flex-1 bg-darkCard border border-slate-800 px-4 py-3 rounded-xl text-sm outline-none focus:border-accentMint" />
              <button type="submit" className="px-5 bg-accentMint hover:bg-accentEmerald text-textWhite font-bold rounded-xl text-sm transition-all">Consult AI</button>
            </form>
          </div>
        )}

        {activeTab === 'profile' && (
          <div className="max-w-xl flex flex-col gap-6 bg-darkCard border border-slate-800 p-6 rounded-2xl">
            <div className="flex flex-col gap-1.5">
              <h2 className="text-xl font-bold text-textWhite">Clinical Profile Shield</h2>
              <p className="text-xs text-textGray">Customize standard dermatology factors to filter out personal allergens.</p>
            </div>

            <form onSubmit={saveProfile} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1">
                <label className="text-xs text-textGray font-bold uppercase">Evaluator Name</label>
                <input value={user.name} onChange={(e) => setUser({...user, name: e.target.value})} type="text" className="w-full bg-[#0F172A] border border-slate-800 px-3.5 py-2 rounded-xl text-sm text-textWhite focus:border-accentMint outline-none" />
              </div>

              <div className="flex flex-col gap-1">
                <label className="text-xs text-textGray font-bold uppercase">Basic Dermatology Group</label>
                <select value={skinType} onChange={(e) => setSkinType(e.target.value)} className="w-full bg-[#0F172A] border border-slate-800 px-3.5 py-2 rounded-xl text-sm text-textWhite outline-none">
                  <option>Normal</option>
                  <option>Sensitive</option>
                  <option>Oily</option>
                  <option>Dry</option>
                  <option>Combination</option>
                </select>
              </div>

              <div className="flex flex-col gap-2">
                <label className="text-xs text-textGray font-bold uppercase">Identify Personal Allergens</label>
                <div className="grid grid-cols-2 gap-2">
                  {['Salicylic Acid', 'Fragrance', 'Sulfates', 'Ketoconazole'].map(item => (
                    <button key={item} type="button" onClick={() => toggleAllergy(item)} className={`border px-3 py-1.5 rounded-xl text-xs font-semibold text-left truncate ${allergies.includes(item) ? 'bg-red-500/10 border-red-500/40 text-red-400' : 'bg-[#0F172A] border-slate-800 hover:border-slate-700'}`}>
                      {item}
                    </button>
                  ))}
                </div>
              </div>

              <label className="flex items-center gap-2 cursor-pointer text-xs text-textGray mt-2">
                <input type="checkbox" checked={pregnancyStatus} onChange={(e) => setPregnancyStatus(e.target.checked)} className="rounded border-slate-800 bg-[#0F172A] text-accentMint focus:ring-accentMint" />
                Trigger Pregnancy Contradiction Flagging
              </label>

              <button type="submit" className="w-full h-11 bg-accentMint hover:bg-accentEmerald text-textWhite font-bold rounded-xl text-sm mt-3 transition-all">Save Health Directives</button>
            </form>
          </div>
        )}
      </main>

      {/* Ingredient Intelligence Dialog */}
      {selectedIngredient && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50 animate-fade-in">
          <div className="w-full max-w-lg bg-darkCard border border-slate-800 p-6 rounded-2xl flex flex-col gap-4">
            <div className="flex justify-between items-center border-b border-slate-800 pb-3">
              <div>
                <h3 className="text-md font-bold text-textWhite">Formulation Intelligence</h3>
                <h2 className="text-xl font-extrabold text-[#10B981] mt-0.5">{selectedIngredient.name}</h2>
              </div>
              <button onClick={() => setSelectedIngredient(null)} className="text-xs text-textGray hover:text-textWhite font-bold">Dismiss</button>
            </div>

            <p className="text-sm text-textGray italic">"{selectedIngredient.scientificDescription}"</p>

            <div className="flex flex-col gap-2 bg-[#0F172A] border border-slate-800 p-4 rounded-xl">
              <div className="flex justify-between text-xs my-0.5">
                <span className="text-textGray">Dermatology Action:</span>
                <span className="font-bold text-textWhite text-right">{selectedIngredient.function}</span>
              </div>
              <div className="flex justify-between text-xs my-0.5">
                <span className="text-textGray">Comedogenic Score:</span>
                <span className="font-bold text-textWhite">{selectedIngredient.comedogenicRating} / 5</span>
              </div>
              <div className="flex justify-between text-xs my-0.5">
                <span className="text-textGray">Pregnancy Rating:</span>
                <span className={`font-bold ${selectedIngredient.pregnancySafetyStatus === 'Avoid' ? 'text-clinicalRed' : 'text-accentMint'}`}>{selectedIngredient.pregnancySafetyStatus}</span>
              </div>
              <div className="flex justify-between text-xs my-0.5">
                <span className="text-textGray">Global Risk:</span>
                <span className={`font-bold ${selectedIngredient.riskLevel === 'High Risk' ? 'text-clinicalRed' : 'text-accentMint'}`}>{selectedIngredient.riskLevel}</span>
              </div>
            </div>

            <div className="flex flex-col gap-1 pt-1.5">
              <h5 className="text-xs font-bold uppercase text-textWhite">Clinical Proven Benefits:</h5>
              <p className="text-xs text-textGray leading-relaxed">{selectedIngredient.benefits}</p>
            </div>

            <div className="flex flex-col gap-1 border-t border-slate-800 pt-3">
              <h5 className="text-xs font-bold uppercase text-clinicalRed">Potential Side Effects:</h5>
              <p className="text-xs text-textGray leading-relaxed">{selectedIngredient.sideEffects}</p>
            </div>
          </div>
        </div>
      )}

      {/* Laser Barcode Simulator Dialog */}
      {showBarcodeDialog && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50 animate-fade-in">
          <div className="w-full max-w-sm bg-darkCard border border-slate-800 p-6 rounded-2xl flex flex-col gap-4">
            <div className="flex justify-between items-center border-b border-slate-800 pb-3">
              <h3 className="text-sm font-bold text-textWhite flex items-center gap-1.5"><QrCode size={18} className="text-accentMint" /> Laser Barcode Simulator</h3>
              <button onClick={() => setShowBarcodeDialog(false)} className="text-xs text-textGray hover:text-textWhite font-bold">Cancel</button>
            </div>

            <p className="text-xs text-textGray leading-relaxed">Position standard medicinal barcode sticker inside the animated viewfinder focus box below.</p>

            {/* Viewfinder simulation with horizontal sweeping red laser line */}
            <div className="h-32 bg-[#0F172A] border border-emerald-500/20 rounded-xl relative overflow-hidden flex flex-col justify-center items-center">
              {/* Simulated static barcode drawing stripes */}
              <div className="flex justify-center items-end h-14 gap-1 select-none">
                <div className="w-0.5 h-full bg-slate-400"></div>
                <div className="w-1.5 h-full bg-slate-400"></div>
                <div className="w-0.5 h-full bg-slate-400"></div>
                <div className="w-2 h-full bg-slate-400"></div>
                <div className="w-1 h-full bg-slate-400"></div>
                <div className="w-0.5 h-full bg-slate-400"></div>
                <div className="w-2 h-full bg-slate-400"></div>
                <div className="w-0.5 h-full bg-slate-400"></div>
                <div className="w-1.5 h-full bg-slate-400"></div>
              </div>
              <span className="text-[10px] text-textGray mt-1 select-none font-mono">UPC-A: 8 901030 704900</span>

              {/* Laser beam */}
              <div className="absolute top-0 left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-red-500 to-transparent scanning-beam"></div>
            </div>

            <div className="flex flex-col gap-2 mt-2">
              <h5 className="text-xs font-bold text-textWhite uppercase mb-1">Pick a barcode to simulate scanning:</h5>
              {[
                { title: 'Pears Soap Bar (UPC: 8901030704900)', key: 'BARCODE_PEARS' },
                { title: 'Himalaya Acne Wash (UPC: 8901396388417)', key: 'BARCODE_HIMALAYA' },
                { title: 'Vicco Skin Cream (UPC: 8901207040116)', key: 'BARCODE_VICCO' }
              ].map(item => (
                <button key={item.key} onClick={() => { setShowBarcodeDialog(false); handleAIAnalysis(item.key); }} className="w-full bg-[#0F172A] hover:bg-[#1E293B]/60 text-left border border-slate-800 px-3.5 py-2.5 rounded-xl text-xs font-semibold text-textWhite select-none transition-all">
                  {item.title}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
