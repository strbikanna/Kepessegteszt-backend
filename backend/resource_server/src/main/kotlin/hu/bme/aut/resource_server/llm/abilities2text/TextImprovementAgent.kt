package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.service.SystemMessage

interface TextImprovementAgent {
    @SystemMessage("""
        Formázd ezt a szöveget html formátumba úgy, hogy a bekezdések <p> tag-ek közé kerüljenek,
        a címsorok <h2> tag-ek közé.
        Töröld a speciális karaktereket is.
        Töröld az olyan szövegrészeket, amiket a szöveg írója közvetlenül mond. Például
        "Természetesen segítek...", "Ez a válasz...", "Elkészítettem az elemzést...", "Elnézést a hibáért..." stb.
        Példa output: <h2>Cím</h2> <h3>Alcím</h3> <p>Első bekezdés.</p><p>Második bekezdés.</p>
        Nem szükséges a <html>, <body> tag-ek használata. Semmi más ne legyen a válaszban csak a formázott szöveg.
    """)
    fun improveText(text: String): String
}