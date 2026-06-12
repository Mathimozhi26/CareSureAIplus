package com.example.data

object ProductSeedingData {

    fun getStarterIngredients(): List<IngredientEntity> {
        return listOf(
            IngredientEntity(
                name = "Niacinamide",
                function = "Skin Brightening & Oil Control",
                benefits = "Fades dark spots, strengthens barrier, reduces sebum, smooths skin texture",
                sideEffects = "Mild redness or tingling if used in high concentrations",
                riskLevel = "Safe",
                scientificDescription = "Vitamin B3 active that works with natural substances in skin to visibly minimize pores and improve uneven skin tone.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Salicylic Acid",
                function = "Acne Treatment & Exfoliation",
                benefits = "Unclogs pores, dissolves dead skin, reduces blackheads & whiteheads, controls acne",
                sideEffects = "Dryness, localized peeling, mild burning sensation, sun sensitivity",
                riskLevel = "Safe",
                scientificDescription = "A Beta-Hydroxy Acid (BHA) that penetrates deeply into pores to dissolve sebum build-up and dead cells.",
                comedogenicRating = 1,
                pregnancySafetyStatus = "Avoid"
            ),
            IngredientEntity(
                name = "Hyaluronic Acid",
                function = "Intense Hydration & Humectant",
                benefits = "Plumps skin, retains deep moisture, smooths dry fine lines, hydrates skin layers",
                sideEffects = "Generally none. Can draw moisture out of skin in extremely dry climates if not sealed",
                riskLevel = "Safe",
                scientificDescription = "A naturally occurring polysaccharide that can hold up to 1000 times its weight in water molecules.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Retinol",
                function = "Anti-Aging & Cellular Renewal",
                benefits = "Speeds cell turnover, boosts collagen production, fades deep wrinkles, improves texture",
                sideEffects = "Redness, dry patches, burning, purging phase (temporary acne flareups), sun sensitivity",
                riskLevel = "Moderate",
                scientificDescription = "A derivative of Vitamin A that acts directly on skin receptors to promote cellular growth and regeneration.",
                comedogenicRating = 2,
                pregnancySafetyStatus = "Avoid"
            ),
            IngredientEntity(
                name = "Ceramides",
                function = "Barrier Repair & Protection",
                benefits = "Locks in deep moisture, repairs damaged skin barrier, prevents water loss, calms irritation",
                sideEffects = "Virtually none, as it is a skin-identical lipid component",
                riskLevel = "Safe",
                scientificDescription = "Lipid molecules found in high concentrations in the uppermost layer of human skin, vital for maintaining barrier integrity.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Zinc Oxide",
                function = "Physical UV Filter & Soothing Agent",
                benefits = "Broad-spectrum UV protection, blocks UVA/UVB rays, calms eczema, reduces inflammatory acne",
                sideEffects = "Can produce a slight white cast on dark skin tones",
                riskLevel = "Safe",
                scientificDescription = "A physical sun reflector that sits on top of the skin, offering immediately effective, non-irritating sun protection.",
                comedogenicRating = 1,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Vitamin C",
                function = "Antioxidant & Glowing Active",
                benefits = "Fights free radical damage, minimizes sunspots, boosts skin radiance, evens out pigmentation",
                sideEffects = "Mild burning, tingling on application, can oxidize quickly if exposed to heat/sun",
                riskLevel = "Safe",
                scientificDescription = "L-Ascorbic Acid or stabilized derivatives, a powerful antioxidant that prevents oxidation and halts melanin synthesis.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Alpha Arbutin",
                function = "Skin Brightening & Pigmentation",
                benefits = "Blocks melanin formation, lightens acne scars, reduces melasma, fades dark spots safely",
                sideEffects = "Rare skin irritation or redness",
                riskLevel = "Safe",
                scientificDescription = "A botanical extract derived from the bearberry plant, acting as a natural, highly efficient dark-spot fader.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Glycolic Acid",
                function = "Chemical Exfoliation (AHA)",
                benefits = "Sheds surface dead cells, treats hyperpigmentation, reveals instant radiant glow, smooths bumps",
                sideEffects = "Skin tingling, peeling, dryness, high sun sensitivity",
                riskLevel = "Moderate",
                scientificDescription = "An Alpha-Hydroxy Acid (AHA) with the smallest molecular size, allowing deep superficial exfoliation.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Centella Asiatica",
                function = "Anti-inflammatory & Calmative",
                benefits = "Rapidly calms sensitized red skin, accelerates minor wound healing, repairs skin elasticity",
                sideEffects = "Extremely rare allergic reactions",
                riskLevel = "Safe",
                scientificDescription = "An ancient medicinal herb (also called Tiger Grass or Cica) famous for boosting cellular healing and calming inflammation.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Minoxidil",
                function = "Hair Loss Treatment & Vasodilator",
                benefits = "Stimulates dormant hair follicles, triggers hair regrowth, thickens thinning hair",
                sideEffects = "Scalp irritation, contact dermatitis, unwanted facial hair growth, systemic palpitations if over-absorbed",
                riskLevel = "High Risk",
                scientificDescription = "A clinically proven chemical compound that dilates miniscule scale blood vessels, carrying nutrients back to hair roots.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Avoid"
            ),
            IngredientEntity(
                name = "Ketoconazole",
                function = "Antifungal shampoo medication",
                benefits = "Clears stubborn dandruff, targets Seborrheic Dermatitis, controls itching scalp fungus",
                sideEffects = "Scalp dryness, texture alterations in hair strands, mild burning sensation",
                riskLevel = "Moderate",
                scientificDescription = "An antimycotic agent that stops the bio-synthesis of fungal cell walls, curing localized scalp flaking.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Paracetamol",
                function = "Pain Relief & Antipyretic",
                benefits = "Brings down high body temperature, alleviates headaches, body aches, and post-fever pains",
                sideEffects = "Hepatotoxicity if daily prescribed limits are exceeded",
                riskLevel = "Safe",
                scientificDescription = "An essential analgesic medicine that blocks chemical prostaglandins in brain centers to control fever and pain impulses.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            ),
            IngredientEntity(
                name = "Ibuprofen",
                function = "Anti-inflammatory & Painkiller",
                benefits = "Reduces localized inflammatory swelling, stops dental pain, arthritic stiffness, and joint soreness",
                sideEffects = "Stomach irritation, acidity, long term kidney burden with prolonged misuse",
                riskLevel = "Moderate",
                scientificDescription = "A Non-Steroidal Anti-Inflammatory Drug (NSAID) which inhibits COX enzyme pathways to reduce swelling and pain.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Avoid"
            ),
            IngredientEntity(
                name = "Lactobacillus Ferment",
                function = "Probiotic Skin Balance",
                benefits = "Strengthens microbiome, improves acne resistance, hydrates and balances pH",
                sideEffects = "None",
                riskLevel = "Safe",
                scientificDescription = "A biotech-derived yeast ferment filtrate that stabilizes surface healthy flora.",
                comedogenicRating = 0,
                pregnancySafetyStatus = "Safe"
            )
        )
    }

    fun generate100Products(): List<ProductEntity> {
        val list = mutableListOf<ProductEntity>()

        // 1. High fidelity baseline products (Skincare, Haircare, Medicine)
        val highFidelityBase = listOf(
            // Skincare standard
            ProductEntity(
                name = "Ultra Light Squalane Hydrator",
                brand = "Minimalist",
                category = "Skincare",
                ingredients = "Hyaluronic Acid, Squalane, Centella Asiatica, Water, Glycerin",
                benefits = "Deep lightweight hydration, repairs barrier, calms redness, locks in moisture without clog",
                warnings = "None. Safe for daily use. Perform spot-test before first use.",
                safetyScore = 95,
                imageUrl = "squalane",
                suitabilityTags = "Dry Skin, Sensitive Skin, Oily Skin, Pregnancy Safe"
            ),
            ProductEntity(
                name = "10% Niacinamide Face Serum",
                brand = "Minimalist",
                category = "Skincare",
                ingredients = "Niacinamide, Zinc PCA, Centella Asiatica, Hyaluronic Acid, Water, Phenoxyethanol",
                benefits = "Controls sebum, fades dark acne spots, minimizes pore appearance, improves uneven tone",
                warnings = "Avoid mixing directly with topical Pure Vitamin C. Keep out of reach of children.",
                safetyScore = 90,
                imageUrl = "niacinamide_min",
                suitabilityTags = "Oily Skin, Acne-Prone Skin, Combination Skin, Sensitive, Pregnancy Safe"
            ),
            ProductEntity(
                name = "2% Salicylic Acid Cleanser",
                brand = "The Derma Co",
                category = "Skincare",
                ingredients = "Salicylic Acid, Chamomile Extract, Allantoin, Glycerin, Coco Glucoside",
                benefits = "Exfoliates deep inside pores, cures blackheads, heals whiteheads, controls ongoing breakouts",
                warnings = "Can cause dry peeling. Avoid near eyes. Apply sunscreen in morning.",
                safetyScore = 80,
                imageUrl = "salicylic_cleanser",
                suitabilityTags = "Oily Skin, Acne-Prone Skin, Avoid: Pregnancy"
            ),
            ProductEntity(
                name = "C-Glow Vitamin C Face Serum",
                brand = "Plum",
                category = "Skincare",
                ingredients = "Vitamin C, Alpha Arbutin, Mandarin Extract, Hyaluronic Acid, Water",
                benefits = "Gives instant glow, fights photo-aging, reduces hyperpigmentation, brightens dark patches",
                warnings = "Keep in dark cool drawer to halt oxidation. Wear broad-spectrum sunscreen in daytime.",
                safetyScore = 88,
                imageUrl = "vitc_plum",
                suitabilityTags = "Dry Skin, Dull Skin, Combination Skin, Aging Skin, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Watermelon Superglow Gel Sunscreen SPF 50",
                brand = "Aqualogica",
                category = "Skincare",
                ingredients = "Zinc Oxide, Titanium Dioxide, Watermelon Extract, Hyaluronic Acid, Water",
                benefits = "Very broad spectrum UVA/UVB defense, deep hydration, completely non-sticky gel, cooling finish",
                warnings = "Reapply every 2 hours if outdoors. Do not ingest.",
                safetyScore = 92,
                imageUrl = "watermelon_sunscreen",
                suitabilityTags = "Dry Skin, Oily Skin, Combination Skin, Normal Skin, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Centella Soothing Moisturizer",
                brand = "Dot & Key",
                category = "Skincare",
                ingredients = "Centella Asiatica, Ceramides, Aloe Vera Extract, Glyceryl Stearate, Water",
                benefits = "Instantly heals inflamed skin, repairs protective barrier, cools heat rashes, intensely moisturizes",
                warnings = "For topical use only. Stop usage if red rash develops.",
                safetyScore = 96,
                imageUrl = "cica_dotkey",
                suitabilityTags = "Sensitive Skin, Dry Skin, Irritated Skin, Eczema Safe, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Jeju Volcanic Ash Pore Cleanser",
                brand = "Pilgrim",
                category = "Skincare",
                ingredients = "Volcanic Ash Extract, White Lotus, Salicylic Acid, Coco Glucoside, Glycerin",
                benefits = "Sucks out excessive sebum, dissolves stubborn mud from facial cells, clears acne roots, deep purifies",
                warnings = "Limit use to twice a week if skin is dry. Wear sunblock.",
                safetyScore = 82,
                imageUrl = "jeju_pilgrim",
                suitabilityTags = "Oily Skin, Extremely Clogged Skin, Avoid: Sensitive, Avoid: Pregnancy"
            ),
            ProductEntity(
                name = "Gentle Skin Cleanser",
                brand = "Cetaphil",
                category = "Skincare",
                ingredients = "Water, Cetyl Alcohol, Propylene Glycol, Sodium Lauryl Sulfate, Stearyl Alcohol, Methylparaben",
                benefits = "Dermatologically tested soap-free formula, retains moisture, washes dirt delicately without strip",
                warnings = "Do not scrub. Safe for eczematous pediatric skin.",
                safetyScore = 94,
                imageUrl = "cetaphil_gentle",
                suitabilityTags = "Sensitive Skin, Extremely Dry Skin, Atopic Dermatitis, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Sensibio H2O Micellar Water",
                brand = "Bioderma",
                category = "Skincare",
                ingredients = "Water, PEG-6 Caprylic Glycerides, Mannitol, Xylitol, Rhamnose, Cucumber Fruit Extract",
                benefits = "Safely captures makeup, heavy city grime, and microparticles while soothing skin reactivity",
                warnings = "Do not rinse with hot water. Eye touch safe.",
                safetyScore = 98,
                imageUrl = "bioderma_sensibio",
                suitabilityTags = "Sensitive Skin, Hyper-reactive Skin, Dry Skin, All Skin Types, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Tea Tree Anti-Acne Oil",
                brand = "Mamaearth",
                category = "Skincare",
                ingredients = "Tea Tree Oil, Salicylic Acid, Neem Extract, Licorice Extract, Water",
                benefits = "Natural antibacterial forces clear cystic spots, heals inflammation residue, balances oils",
                warnings = "Slight tingling is normal. Keep away from direct nostrils.",
                safetyScore = 85,
                imageUrl = "teatree_mama",
                suitabilityTags = "Oily Skin, Active Acne Skin, Avoid: Extremely Sensitive"
            ),
            // Haircare standard
            ProductEntity(
                name = "Anti-Dandruff Ketoconazole Shampoo",
                brand = "Be Bodywise",
                category = "Haircare",
                ingredients = "Ketoconazole, Zinc Pyrithione, Aloe Vera Extract, Panthenol, Aqua",
                benefits = "Clears stubborn dandruff flakes, treats itchy scaling scalp, repairs damaged dry follicles",
                warnings = "Keep out of eyes. Avoid continuous daily use; use twice a week.",
                safetyScore = 80,
                imageUrl = "shampoo_bodywise",
                suitabilityTags = "Scalp Flakes, Seborrheic Dermatitis, Itchy Scalp, Pregnancy Safe"
            ),
            ProductEntity(
                name = "5% Minoxidil Hair Regrowth Tonic",
                brand = "Be Bodywise",
                category = "Haircare",
                ingredients = "Minoxidil, Ethanol, Propylene Glycol, Aqua",
                benefits = "Promotes new hair cell multiplication, stops temporal hair fall, reactivates blood follicle nodes",
                warnings = "Do not ingest. Do not apply on irritated scalp. strictly avoid if pregnant.",
                safetyScore = 65,
                imageUrl = "minoxidil_bodywise",
                suitabilityTags = "Severe Hair Loss, Male Pattern Baldness, Alopecia, Avoid: Pregnancy"
            ),
            ProductEntity(
                name = "Onion Hair Oil for Growth",
                brand = "Mamaearth",
                category = "Haircare",
                ingredients = "Onion Seed Oil, Redensyl, Almond Oil, Castor Oil, Vitamin E",
                benefits = "Controls excess hair breakage, adds shine, strengthens hair shafts, repairs split ends",
                warnings = "Wash thoroughly to prevent product buildup on oily scalps.",
                safetyScore = 95,
                imageUrl = "onion_oil",
                suitabilityTags = "Thin Hair, Damaged Hair, All Scalp Types, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Macadamia Smoothing Hair Mask",
                brand = "Plum",
                category = "Haircare",
                ingredients = "Macadamia Oil, Shea Butter, Panthenol, Glycerin, Behentrimonium Chloride",
                benefits = "Supplies deep softness to frizzy coarse hair, heals deep chemical styling heat burns",
                warnings = "Do not apply directly on hair roots or scalp pores to avoid clogging.",
                safetyScore = 92,
                imageUrl = "plum_macadamia",
                suitabilityTags = "Frizzy Hair, Dry Hair, Colored Hair, Coarse Hair"
            ),
            // Medicine standard
            ProductEntity(
                name = "Suncote SPF 30 Gel",
                brand = "Fixderma",
                category = "Medicine",
                ingredients = "Octyl Methoxycinnamate, Avobenzone, Titanium Dioxide, Water, Carbomer",
                benefits = "Pharmacist-prescribed anti-clogging dry touch acne-safe sun gel block.",
                warnings = "Apply at least 15 minutes before stepping out in direct sun.",
                safetyScore = 90,
                imageUrl = "suncote_fixderma",
                suitabilityTags = "Acne-prone skin, Extreme Oily Skin, Normal Skin, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Caladew Calamine Lotion",
                brand = "Fixderma",
                category = "Medicine",
                ingredients = "Calamine, Zinc Oxide, Aloe Vera, Cucumber Extract, Bentonite Clay",
                benefits = "Medical defense heals chickenpox itch, severe sun burns, mosquito bites, and skin heat hives",
                warnings = "Shake well before using. External application only.",
                safetyScore = 94,
                imageUrl = "caladew_lotion",
                suitabilityTags = "Irritated Skin, Sunburns, Heat Rash, Itch, Pregnancy Safe"
            ),
            ProductEntity(
                name = "Moiz Cleansing Lotion",
                brand = "Fixderma",
                category = "Medicine",
                ingredients = "Cetyl Alcohol, Stearyl Alcohol, Water, Glycerin, Sodium Cocoyl Apple Amino Acids",
                benefits = "Hospital grade super delicate cleanser for post-laser or burned skin recovery.",
                warnings = "Store in cool dry space.",
                safetyScore = 96,
                imageUrl = "moiz_cleanser",
                suitabilityTags = "Compromised Barrier, Post-Treatment Skin, Pediatric Atopic Skin"
            ),
            ProductEntity(
                name = "Crocin Pain Relief Tablet",
                brand = "Himalaya", // Just mapping to requested brand for local simplicity
                category = "Medicine",
                ingredients = "Paracetamol, Caffeine",
                benefits = "Acts lightning fast on heavy stress headaches, migraines, high tooth aches",
                warnings = "Contains caffeine. Can cause insomnia if taken in night. Strict limit: 3 tablets/day.",
                safetyScore = 85,
                imageUrl = "crocin_med",
                suitabilityTags = "Migraine, Muscle Aches, Fever, Pain, Avoid: Pregnancy"
            ),
            ProductEntity(
                name = "Liv.52 Liver Care Daily",
                brand = "Himalaya",
                category = "Medicine",
                ingredients = "Himsra Powder, Kasani Powder, Mandur Bhasma, Biranjasipha Powder",
                benefits = "Ayurvedic liver protection, neutralizes medication toxins, enhances daily food absorption",
                warnings = "Consult herbal physician. Keep in air tight casing.",
                safetyScore = 90,
                imageUrl = "liv52",
                suitabilityTags = "Liver Support, Digestion Boost, Healthy Organs, Organic"
            )
        )

        list.addAll(highFidelityBase)

        // Brands lists
        val brands = listOf(
            "Dot & Key", "Pilgrim", "Garnier", "Minimalist", "Mamaearth",
            "Plum", "Cetaphil", "Bioderma", "CeraVe", "Fixderma",
            "Foxtale", "Aqualogica", "Lakme", "Nivea", "Himalaya",
            "Dove", "Ponds", "Wow Skin Science", "Be Bodywise", "The Derma Co"
        )

        // Categories list
        val categories = listOf("Skincare", "Haircare", "Medicine")

        // In order to expand the dataset dynamically to EXACTLY 105 products (making > 100 as requested):
        var count = list.size
        var brandIndex = 0
        var categoryIndex = 0

        val productDescriptors = listOf(
            Triple("Hydrating Rose Water Toner", "Rose Water, Glycerin, Aloe Vera, Allantoin, Panthenol", "Restores ideal skin pH, balances moisture pores, calms post-wash tightness"),
            Triple("Nourishing Vitamin E Nourisher", "Vitamin E, Almond Oil, Sesame Oil, Honey, Water", "Deeply moisturizes cellular layers, repairs rough dry flake lines, skin glowing"),
            Triple("Brightening Papaya Face Scrub", "Papaya Extract, Walnut Shell Dust, Glycerin, Water", "Delicately polishes surface dirt and dead cells, opens fresh glowing skin looks"),
            Triple("Onion Scalp Nutrition Mask", "Onion Seed Extract, Keratin Protein, Coconut Oil, Panthenol", "Recharges lifeless breakage hair strands, feeds root follicle keratin"),
            Triple("Tea Tree Dandruff Guard Oil", "Tea Tree Extract, Neem Leaf Oil, Rosemary Oil, Mineral Oil", "Targets flake-producing scalp fungus, cools chronic root itch areas"),
            Triple("Pure Aloe Vera Calming Gel", "Aloe Vera Juice, Cucumber Extract, Carbomer, Glycerin", "Instantly hydrates sun-burned parts, cools inflammation, oil-free moisturizer"),
            Triple("Acne Spot Corrector Paste", "Salicylic Acid, Niacinamide, Sulfur Clay, Zinc Oxide, Tea Tree", "Dries up active red pimples overnight, pulls out pus safely without scarring"),
            Triple("Multi-Peptide Lash Renew Tonic", "Peptides, Biotin, castor seed oil, Hyaluronic Acid", "Strengthens frail thin lash follicles, boosts thickness and growth"),
            Triple("Avocado Ultra Deep Nourishing Mask", "Avocado paste oil, Honey, Shea Butter, Ceramides, Glycerin", "Re-fills protective lipid lost layers, shields dry cracked windburnt skin"),
            Triple("Volcanic Sulfur Pore Minimizer", "Sulfur, Kaolin Clay, Bentonite, Salicylic Acid, Glycerin", "Absorbs excessive nose blackheads, controls grease overflow channels"),
            Triple("Saffron Glow Radiance Cream", "Kumkumadi Tailam, Saffron Extract, Licorice Powder, Rose Extract", "Ayurvedic brightening cream, clears sun-tan lines, hydrates face skin"),
            Triple("Daily Sun Protection Milk SPF30", "Titanium Dioxide, Green Tea Extract, Zinc Oxide, Water, Stearic Acid", "Extremely light watery daily outdoor UV shield with green tea repair"),
            Triple("Charcoal Deep Face Wash", "Active Charcoal, Salicylic Acid, Spearmint Oil, Lauric Acid", "Magnetically extracts deep industrial pollution dust particles from face pores"),
            Triple("Green Tea Night Clarifying Gel", "Green Tea Extracts, Glycolic Acid, Willow Bark, Water", "Mildly digests micro skin bumps during sleep, blocks morning oiliness"),
            Triple("Coffee Antioxidant Undereye Gel", "Caffeine, Cucumber Extract, Vitamin E, Hyaluronic Acid, Aqua", "Constricts swollen blood vessels, reduces morning eye puffiness, drains dark shadows"),
            Triple("Biotin Hair Thickening Lotion", "Biotin, Keratin, Caffeine, Argan Oil, Water", "Supplies direct nutrition to thin hair roots, coats hair shafts for instant volume"),
            Triple("Gentle Lavender Body Wash", "Lavender Extract, Coco Glucoside, Glycerin, Jojoba Oil", "Chemical-free relaxing bath experience, cleanses with soft dense smooth foam"),
            Triple("Bha Clear Pore Pads", "Salicylic Acid, Allantoin, Glycolic Acid, Water, Glycerin", "Convenient facial pads to dissolve oil traps, blackheads, and dead flakes on the go"),
            Triple("Niacinamide Body Moisturizer SPF15", "Niacinamide, Shea Butter, physical UV filters, Glycerin", "Fades body sun lines, restores smooth soft hand and limb skin texture"),
            Triple("Coconut Milk Super Damage Recovery", "Coconut Milk Extract, Wheat Protein, Argan Oil, Panthenol", "Replenishes moisture inside rough frizzy hair cuticles dry from straightener damage")
        )

        // Loop to generate variations to reach > 100 products
        while (count < 105) {
            val descriptor = productDescriptors[count % productDescriptors.size]
            val brand = brands[brandIndex % brands.size]
            val category = categories[categoryIndex % categories.size]

            // Customize tags
            val tags = when {
                category == "Skincare" && descriptor.first.contains("Acne") -> "Oily Skin, Acne-Prone Skin, Avoid: Pregnancy"
                category == "Skincare" && descriptor.first.contains("Hydrating") -> "Dry Skin, Sensitive Skin, Normal Skin, Pregnancy Safe"
                category == "Skincare" && descriptor.first.contains("Glow") -> "Normal Skin, Combination Skin, Dull Skin"
                category == "Haircare" -> "Thin Hair, Dry Hair, Scalp Care"
                else -> "General Health, Wellness Support"
            }

            val safetyScoreVal = 80 + (count % 19)

            list.add(
                ProductEntity(
                    name = brand + " " + descriptor.first,
                    brand = brand,
                    category = category,
                    ingredients = descriptor.second,
                    benefits = descriptor.third,
                    warnings = "Keep out of direct sunlight. Always patch test before skin coverage.",
                    safetyScore = safetyScoreVal,
                    imageUrl = "gen_prod_${count}",
                    suitabilityTags = tags
                )
            )

            // Increment pointers
            count++
            brandIndex++
            if (count % 3 == 0) categoryIndex++
        }

        return list
    }
}
