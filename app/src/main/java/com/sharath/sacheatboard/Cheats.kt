package com.sharath.sacheatboard

/**
 * Android (mobile) version cheats: type the full phrase.
 * The short PC hash codes (HESOYAM etc.) are not used on mobile.
 * A successful entry shows "Cheat Activated".
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
        Cheat(P, "Health, armor, $250,000 + repairs car", "INEEDSOMEHELP", note = "May fail some missions"),
        Cheat(P, "Semi-infinite health", "NOONECANHURTME"),
        Cheat(P, "Infinite lung capacity", "MANFROMATLANTIS"),
        Cheat(P, "Suicide", "GOODBYECRUELWORLD"),
        Cheat(P, "Jetpack", "ROCKETMAN"),
        Cheat(P, "Parachute", "LETSGOBASEJUMPING"),
        Cheat(P, "Adrenaline mode", "ANOSEONGLASS"),
        Cheat(P, "Mega jump", "KANGAROO"),
        // Stats
        Cheat(ST, "Max stamina", "ICANGOALLNIGHT"),
        Cheat(ST, "Max hitman level (weapons)", "PROFESSIONALKILLER"),
        Cheat(ST, "Max vehicle skills", "NATURALTALENT"),
        Cheat(ST, "Max respect", "WORSHIPME"),
        Cheat(ST, "Max sex appeal", "HELLOLADIES"),
        Cheat(ST, "Fat body", "WHOATEALLTHEPIES"),
        Cheat(ST, "Muscular body", "BUFFMEUP"),
        Cheat(ST, "Skinny body", "LEANANDMEAN"),
        // Weapons
        Cheat(W, "Weapon set 1 (Thug's)", "THUGSARMOURY"),
        Cheat(W, "Weapon set 2 (Professional)", "PROFESSIONALSKIT"),
        Cheat(W, "Weapon set 3 (Nutter)", "NUTTERSTOYS"),
        Cheat(W, "Infinite ammo, no reload", "FULLCLIP"),
        // Wanted
        Cheat(WL, "Clear wanted level", "TURNDOWNTHEHEAT"),
        Cheat(WL, "Wanted level +2 stars", "TURNUPTHEHEAT"),
        Cheat(WL, "Disable wanted level", "IDOASIPLEASE"),
        Cheat(WL, "Six-star wanted level", "BRINGITON"),
        // Vehicles
        Cheat(V, "Spawn Rhino", "TIMETOKICKASS"),
        Cheat(V, "Spawn Hydra", "JUMPJET"),
        Cheat(V, "Spawn Hunter", "OHDUDE"),
        Cheat(V, "Spawn Stunt Plane", "FLYINGTOSTUNT"),
        Cheat(V, "Spawn Dozer", "ITSALLBULL"),
        Cheat(V, "Spawn Trashmaster", "TRUEGRIME"),
        Cheat(V, "Spawn Tanker", "HITTHEROADJACK"),
        Cheat(V, "Spawn Monster Truck", "MONSTERMASH"),
        Cheat(V, "Spawn Quad", "FOURWHEELFUN"),
        Cheat(V, "Spawn Rancher", "DOUGHNUTHANDICAP"),
        Cheat(V, "Spawn Romero", "WHERESTHEFUNERAL"),
        Cheat(V, "Spawn Vortex", "IWANTTOHOVER"),
        Cheat(V, "Spawn Hotring Racer A", "NOTFORPUBLICROADS"),
        Cheat(V, "Spawn Hotring Racer B", "JUSTTRYANDSTOPME"),
        Cheat(V, "Spawn Bloodring Banger", "OLDSPEEDDEMON"),
        Cheat(V, "Spawn Stretch Limo", "CELEBRITYSTATUS"),
        Cheat(V, "Spawn Caddy", "18HOLES"),
        Cheat(V, "Perfect handling", "STICKLIKEGLUE"),
        Cheat(V, "Nitro on all cars", "SPEEDFREAK"),
        Cheat(V, "Cars blow up easily", "TOUCHMYCARYOUDIE"),
        Cheat(V, "Blow up all cars", "ALLCARSGOBOOM"),
        Cheat(V, "Invisible cars", "WHEELSONLYPLEASE"),
        Cheat(V, "Flying cars", "CHITTYCHITTYBANGBANG"),
        Cheat(V, "Flying boats", "FLYINGFISH"),
        Cheat(V, "Moon-gravity cars", "BUBBLECARS"),
        Cheat(V, "Free aim while driving", "IWANNADRIVEBY"),
        Cheat(V, "Big bunny hop", "CJPHONEHOME"),
        // Weather
        Cheat(WE, "Sunny", "PLEASANTLYWARM"),
        Cheat(WE, "Very sunny", "TOODAMNHOT"),
        Cheat(WE, "Overcast", "DULLDULLDAY"),
        Cheat(WE, "Rain", "STAYINANDWATCHTV"),
        Cheat(WE, "Fog", "CANTSEEWHEREIMGOING"),
        Cheat(WE, "Thunderstorm", "SCOTTISHSUMMER"),
        Cheat(WE, "Sandstorm", "SANDINMYEARS"),
        // Time
        Cheat(T, "Faster clock", "TIMEJUSTFLIESBY"),
        Cheat(T, "Always midnight", "NIGHTPROWLER"),
        Cheat(T, "Orange sky", "DONTBRINGONTHENIGHT"),
        // Traffic & Peds
        Cheat(TP, "All green lights", "DONTTRYANDSTOPME"),
        Cheat(TP, "Aggressive drivers", "ALLDRIVERSARECRIMINALS"),
        Cheat(TP, "Pink cars", "PINKISTHENEWCOOL"),
        Cheat(TP, "Black cars", "SOLONGASITSBLACK"),
        Cheat(TP, "All cheap cars", "EVERYONEISPOOR"),
        Cheat(TP, "All fast cars", "EVERYONEISRICH"),
        Cheat(TP, "Reduced traffic", "GHOSTTOWN"),
        Cheat(TP, "Pedestrians riot with golf clubs", "ROUGHNEIGHBOURHOOD"),
        Cheat(TP, "Pedestrians attack you", "STOPPICKINGONME"),
        Cheat(TP, "Pedestrians are armed", "SURROUNDEDBYNUTTERS"),
        Cheat(TP, "Elvis pedestrians", "BLUESUEDESHOES"),
        Cheat(TP, "Pedestrians attack with guns", "ATTACKOFTHEVILLAGEPEOPLE"),
        Cheat(TP, "Riot mode", "STATEOFEMERGENCY"),
        Cheat(TP, "Beach party", "LIFESABEACH"),
        Cheat(TP, "Gang members mode", "ONLYHOMIESALLOWED"),
        Cheat(TP, "Gang control", "BETTERSTAYINDOORS"),
        Cheat(TP, "Ninja theme", "NINJATOWN"),
        Cheat(TP, "Pimp mode", "LOVECONQUERSALL"),
        Cheat(TP, "Funhouse mode", "CRAZYTOWN"),
        Cheat(TP, "Rural theme", "HICKSVILLE"),
        Cheat(TP, "Recruit anyone: pistols", "WANNABEINMYGANG"),
        Cheat(TP, "Recruit anyone: rocket launchers", "ROCKETMAYHEM"),
        Cheat(TP, "Recruit anyone: AK-47", "NOONECANSTOPUS"),
        // Gameplay
        Cheat(G, "Fast motion", "SPEEDITUP"),
        Cheat(G, "Slow motion", "SLOWITDOWN")
    )
}
