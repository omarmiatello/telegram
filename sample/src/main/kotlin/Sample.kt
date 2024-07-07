import com.github.omarmiatello.telegram.*

fun main() {
    val sendMessageRequest = TelegramRequest.SendMessageRequest(
        chat_id = ChatId("chat123"),
        text = """There are *23 new demos*: [Bloodstained: Ritual of the Night](https://stadia.google.com/game/bloodstained-ritual-of-the-night) - Play for 60 min - available also with #StadiaPro
[Embr](https://stadia.google.com/game/embr) - Play for 60 min
[Enter The Gungeon](https://stadia.google.com/game/enter-the-gungeon) - Play for 30 min #LocalMultiplayer
[FORECLOSED](https://stadia.google.com/game/foreclosed) - Play for 60 min
[Hello Engineer](https://stadia.google.com/game/hello-engineer) - Play for 30 min
[Hello Neighbor](https://stadia.google.com/game/hello-neighbor) - Play for 60 min
[Hello Neighbor: Hide and Seek](https://stadia.google.com/game/hello-neighbor-hide-and-seek) - Play for 60 min
[Hotline Miami](https://stadia.google.com/game/hotline-miami) - Play for 30 min
[Hotline Miami 2:Wrong Number](https://stadia.google.com/game/hotline-miami-2wrong-number) - Play for 30 min
[Human: Fall Flat Stadia Edition](https://stadia.google.com/game/human-fall-flat-stadia-edition) - Play for 60 min #LocalCoop #LocalMultiplayer
[Kemono Heroes](https://stadia.google.com/game/kemono-heroes) - Play for 30 min #LocalCoop #LocalMultiplayer
[Monster Energy Supercross - The Official Videogame 3](https://stadia.google.com/game/monster-energy-supercross-the-official-videogame-3) - Play for 60 min
[Monster Energy Supercross - The Official Videogame 4](https://stadia.google.com/game/monster-energy-supercross-the-official-videogame-4) - Play for 60 min
[MotoGP™20](https://stadia.google.com/game/motogp20) - Play for 60 min
[Outcasters](https://stadia.google.com/game/outcasters) - Play for 60 min
[PIKUNIKU](https://stadia.google.com/game/pikuniku) - Play for 30 min #LocalCoop
[Reigns](https://stadia.google.com/game/reigns) - Play for 30 min
[Secret Neighbor](https://stadia.google.com/game/secret-neighbor) - Play for 60 min
[Serious Sam 4](https://stadia.google.com/game/serious-sam-4) - Play for 30 min
[Serious Sam Collection](https://stadia.google.com/game/serious-sam-collection) - Play for 30 min
[Terraria](https://stadia.google.com/game/terraria) - Play for 90 min - available also with #StadiaPro
[Time on Frog Island](https://stadia.google.com/game/time-on-frog-island) - Play for 60 min - available also with #StadiaPro
[Ys VIII: Lacrimosa of DANA](https://stadia.google.com/game/ys-viii-lacrimosa-of-dana) - Play for 120 min""",
        parse_mode = ParseMode.MarkdownV2,
        reply_markup = InlineKeyboardMarkup(
            listOf(
                listOf(
                    InlineKeyboardButton("Button1", url = "https://example.com")
                )
            )
        )
    )
    println("Request: ${sendMessageRequest.toJsonForRequest()}")
    println("Response: ${sendMessageRequest.toJsonForResponse()}")

    val jsonString = sendMessageRequest.toJsonForResponse()
    println("Parse: ${TelegramRequest.SendMessageRequest.fromJson(jsonString)}")
}