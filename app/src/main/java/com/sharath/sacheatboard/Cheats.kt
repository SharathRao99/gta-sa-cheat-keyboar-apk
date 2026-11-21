package com.sharath.sacheatboard

/**
 * code   = short keyboard code (PC hash code), always present
 * phrase = full "intended" phrase, optional
 * Many sources disagree on a few entries, so anything uncertain was left out.
 * Verify in game: a successful entry shows "Cheat Activated".
 */
data class Cheat(
    val category: String,
    val name: String,
    val code: String,
    val phrase: String = "",
    val note: String = ""
) {
    val id: String get() = category + "|" + code

    /** What gets typed on tap. */
    fun typed(preferShort: Boolean): String =
        if (preferShort || phrase.isEmpty()) code else phrase

    /** What gets typed on long-press (the other variant). */
    fun alt(preferShort: Boolean): String =
        if (preferShort) (if (phrase.isEmpty()) code else phrase) else code
}

object Cheats {
    const val P = "Player"
    const val ST = "Stats"
    const val W = "Weapons"
    const val WL = "Wanted"
    const val V = "Vehicles"
    const val WE = "Weather"
    const val T = "Time"
    const val TP = "Traffic & Peds"
    const val G = "Gameplay"

    val categories = listOf("All", "★", P, ST, W, WL, V, WE, T, TP, G)

    val all: List<Cheat> = listOf(
        // Player
        Cheat(P, "Health, armor, \$250,000", "HESOYAM", "",
            "Also repairs your vehicle. Can fail Just Business and End of the Line, and damages Lure."),
        Cheat(P, "Unlimited health", "BAGUVIX", "", "Explosions, drowning, falls and police heli fire still hurt."),
        Cheat(P, "Suicide", "SZCMAWO", "GOODBYECRUELWORLD", "Doesn't work with unlimited health."),
        Cheat(P, "Super punch", "IAVENJQ", "", "One-hit kills."),
        Cheat(P, "Mega jump", "LFGMHAL", "KANGAROO"),
        Cheat(P, "Infinite lung capacity", "CVWKXAM", "MANFROMATLANTIS"),
        Cheat(P, "Jetpack", "YECGAA", "ROCKETMAN"),
        Cheat(P, "Parachute", "AIYPWZQP"),
        Cheat(P, "Never hungry", "AEDUWNV"),

        // Stats
        Cheat(ST, "Max fat", "BTCDBCB", "FATTIE"),
        Cheat(ST, "Max muscle", "JYSDSOD", "BUFFMEUP"),
        Cheat(ST, "Min fat & muscle", "KVGYZQK", "LEANANDMEAN"),
        Cheat(ST, "Max respect", "OGXSDAG"),
        Cheat(ST, "Max sex appeal", "EHIBXQS"),
        Cheat(ST, "Max vehicle skills", "VQIMAHA", "NATURALTALENT"),
        Cheat(ST, "Max hitman level (all weapons)", "NCSGDAG", "PROFESSIONALKILLER"),

        // Weapons
        Cheat(W, "Weapon set 1 (Thug's)", "LXGIWYL", "THUGSARMOURY"),
        Cheat(W, "Weapon set 2 (Professional)", "KJKSZPJ", "PROFESSIONALSKIT", "Fails Tagging Up Turf."),
        Cheat(W, "Weapon set 3 (Nutter)", "UZUMYMW", "NUTTERSTOYS"),
        Cheat(W, "Unlimited ammo, no reload", "WANRLTW", "FULLCLIP", "May glitch audio."),

        // Wanted
        Cheat(WL, "Clear wanted level", "ASNAEB", "TURNDOWNTHEHEAT"),
        Cheat(WL, "Wanted level +2 stars", "OSRBLHH", "TURNUPTHEHEAT", "Fails 555 We Tip."),
        Cheat(WL, "Never wanted (locks wanted level)", "AEZAKMI", "LEAVEMEALONE"),
        Cheat(WL, "Six-star wanted level", "LJSPQK", "BRINGITON", "Fails 555 We Tip."),

        // Vehicles
        Cheat(V, "Nitro on all cars", "COXEFGU", "SPEEDFREAK"),
        Cheat(V, "Flying cars", "RIPAZHA", "COMEFLYWITHME"),
        Cheat(V, "Flying boats", "AFSNMSMW", "FLYINGFISH"),
        Cheat(V, "Massive bunny hop", "JHJOECW", "CJPHONEHOME"),
        Cheat(V, "Invisible cars", "XICWMD", "WHEELSONLYPLEASE"),
        Cheat(V, "Explode all vehicles", "CPKTNWT", "BIGBANG", "Bicycles are not affected."),
        Cheat(V, "Cars float away when hit", "BSXSGGC", "BUBBLECARS"),
        Cheat(V, "Smash n' Boom (car destroys others)", "JCNRUAD", "", "Invincibility is unreliable."),
        Cheat(V, "Spawn Rhino", "AIWPRTON"),
        Cheat(V, "Spawn Hydra", "AWPTMIIQ", "JUMPJET"),
        Cheat(V, "Spawn Hunter", "OHDUDE"),
        Cheat(V, "Spawn Stunt Plane", "URKQSRK", "FLYINGTOSTUNT"),
        Cheat(V, "Spawn Dozer", "EEGCYXT", "ITSALLBULL"),
        Cheat(V, "Spawn Trashmaster", "UBHYZHQ", "TRUEGRIME"),
        Cheat(V, "Spawn Tanker + trailer", "AMOMHRER"),
        Cheat(V, "Spawn Monster Truck", "AGBDLCID", "MONSTERMASH"),
        Cheat(V, "Spawn Quad", "AKJJYGLC", "FOURWHEELFUN"),
        Cheat(V, "Spawn Rancher", "JQNTDMH"),
        Cheat(V, "Spawn Romero", "AQTBCODX", "WHERESTHEFUNERAL"),
        Cheat(V, "Spawn Vortex", "KGGGDKP"),
        Cheat(V, "Spawn Hotring Racer", "VPJTQWV", "VROCKPOKEY"),
        Cheat(V, "Spawn Bloodring Banger", "CQZIJMB", "OLDSPEEDDEMON"),
        Cheat(V, "Spawn Caddy", "RZHSUEW"),
        Cheat(V, "Spawn Stretch Limo", "KRIJEBR"),

        // Weather
        Cheat(WE, "Sunny", "AFZLLQLL", "PLEASANTLYWARM"),
        Cheat(WE, "Very sunny", "ICIKPYH", "TOODAMNHOT"),
        Cheat(WE, "Overcast", "ALNSFMZO", "DULLDULLDAY"),
        Cheat(WE, "Rain", "AUIFRVQS", "STAYINANDWATCHTV"),
        Cheat(WE, "Fog", "CFVFGMJ", "CANTSEEWHEREIMGOING"),
        Cheat(WE, "Sandstorm", "CWJXUOC", "SANDINMYEARS"),
        Cheat(WE, "Thunderstorm", "MGHXYRM", "SCOTTISHSUMMER"),

        // Time
        Cheat(T, "Always midnight", "XJVSNAJ", "NIGHTPROWLER"),
        Cheat(T, "Always 21:00, orange sky", "OFVIAC"),
        Cheat(T, "Faster clock (enter again to turn off)", "YSOHNUL"),

        // Traffic & Peds
        Cheat(TP, "Reduced traffic / no pedestrians", "THGLOJ"),
        Cheat(TP, "Aggressive drivers", "YLTEICZ"),
        Cheat(TP, "Beach party theme", "CIKGCGX", "LIFESABEACH"),
        Cheat(TP, "Cheap-car traffic", "BGKGTJH"),
        Cheat(TP, "Black traffic", "IOWDLAC"),
        Cheat(TP, "Pink traffic", "LLQPFBN"),
        Cheat(TP, "Country traffic", "KKZRPKW"),
        Cheat(TP, "Country theme", "FVTMNBZ"),
        Cheat(TP, "Sports-car traffic", "GUSNHDE"),
        Cheat(TP, "Funhouse theme", "PRIEBJ"),
        Cheat(TP, "Triad theme", "AFPHULTL"),
        Cheat(TP, "Pimp theme", "BEKKNQV"),
        Cheat(TP, "Pedestrians riot", "IOJUFZN", "STATEOFEMERGENCY", "Permanent if you save while active."),
        Cheat(TP, "Pedestrians are Elvis", "ASBHGRB"),
        Cheat(TP, "Pedestrians attack you", "BAGOWPG", "", "Permanent if you save while active."),
        Cheat(TP, "Pedestrians attack with guns", "BGLUAWML", "", "Permanent if you save while active."),
        Cheat(TP, "Pedestrians have weapons", "FOOOXFT"),
        Cheat(TP, "Gang members everywhere", "MROEMZH"),
        Cheat(TP, "Only gangs fight (no cops/citizens)", "BIFBUZZ"),
        Cheat(TP, "Recruit anyone: pistols", "SJMAHPE"),
        Cheat(TP, "Recruit anyone: rocket launchers", "ZSOXFSQ"),
        Cheat(TP, "Recruit anyone: AK-47", "BMTPWHR"),

        // Gameplay
        Cheat(G, "Fast gameplay", "PPGWJHT", "ONSPEED"),
        Cheat(G, "Slow gameplay", "LIYOAAY", "BOOOOORING"),
        Cheat(G, "Traffic lights always green", "ZEIIVG"),
        Cheat(G, "Gang wars (Android)", "ONLYHOMIESALLOWED")
    )
}
