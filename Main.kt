// Dungeon Escape Game in Kotlin
// A simple text-based adventure game where the player navigates through rooms, collects items, and fights enemies.

// Room class to represent each location in the dungeon
data class Room(
    // Room properties
    val name: String,
    val description: String,
    val exits: MutableMap<String, Room> = mutableMapOf(),
    val items: MutableList<String> = mutableListOf(),
    var enemy: Enemy? = null
)

// Player class to manage player state
class Player(
    var currentRoom: Room,
    var health: Int = 100
) {
    val inventory = mutableListOf<String>()

    val visitedRooms = mutableSetOf<Room>()

    // Mark the current room as visited
    fun visitCurrentRoom() {
        visitedRooms.add(currentRoom)
    }

    // Move to a different room
    fun move(direction: String) {
        val nextRoom = currentRoom.exits[direction]
        if (nextRoom != null) {
            currentRoom = nextRoom
            println("You move $direction to ${currentRoom.name}.")
            println(currentRoom.description)
        } else {
            println("You can't go that way.")
        }
    }

    // Pick up an item in the current room
    fun take(item: String) {
        if (item in currentRoom.items) {
            inventory.add(item)
            visitCurrentRoom() // mark room as visited
            currentRoom.items.remove(item)
            println("You picked up a $item.")
        } else {
            println("There is no $item here.")
        }
    }

    // Show player's inventory
    fun showInventory() {
        if (inventory.isEmpty()) {
            println("Your inventory is empty.")
        } else {
            println("You are carrying: ${inventory.joinToString(", ")}")
        }
    }
}

// Enemy class to represent adversaries in the dungeon
class Enemy(
    val name: String,
    var health: Int,
    val damage: Int
) {
    // Check if the enemy is still alive
    fun isAlive(): Boolean = health > 0
}

// Game class to manage game state and logic
class Game {
    private lateinit var entrance: Room
    private lateinit var hallway: Room
    private lateinit var treasureRoom: Room

    private lateinit var player: Player

    // Setup the game world
    fun setup() {
        // Rooms
        entrance = Room("Entrance", "You stand at the dungeon entrance. A hallway lies north.")
        hallway = Room("Hallway", "A dimly lit hallway. Paths lead south and east.")
        treasureRoom = Room("Treasure Room", "A room glittering with treasure. But danger lurks here...")

        // Link exits
        entrance.exits["north"] = hallway
        hallway.exits["south"] = entrance
        hallway.exits["east"] = treasureRoom

        // Place items
        hallway.items.add("sword")

        // Place enemy
        treasureRoom.enemy = Enemy("Goblin", 30, 10)

        // Player
        player = Player(currentRoom = entrance)
    }

    // Start the game loop
    fun start() {
        println("Welcome to the Dungeon Escape!")
        println("Type 'help' to see available commands, or 'quit' to exit.\n")
        println(player.currentRoom.description)

        while (true) {
            print("> ")
            val input = readLine()?.trim()?.lowercase() ?: continue
            if (input == "quit") {
                println("Thanks for playing!")
                break
            }
            processCommand(input)
        }
    }

    // Process player commands
    private fun processCommand(command: String) {
        val words = command.split(" ")
        when (words[0]) {
            "go" -> if (words.size > 1) player.move(words[1]) else println("Go where?")
            "take" -> if (words.size > 1) player.take(words[1]) else println("Take what?")
            "inventory" -> player.showInventory()
            "look" -> println(player.currentRoom.description)
            "fight" -> fight()
            "map" -> showMap()
            "help" -> showHelp()
            else -> println("I don't understand that command.")
        }
    }

    // Display help menu
    private fun showHelp() {
        println(
            """
            Available commands:
            - go <direction>   : Move north, south, east, or west
            - take <item>      : Pick up an item in the room
            - inventory        : Show what you're carrying
            - look             : Look around the current room
            - fight            : Fight an enemy if one is present
            - map              : See a map of visited rooms
            - help             : Show this help menu
            - quit             : Exit the game
            """.trimIndent()
        )
    }

    // Display a simple map of visited rooms
    private fun showMap() {
        println("\nDungeon Map (visited rooms marked with *)")
        val allRooms = listOf(entrance, hallway, treasureRoom)
        
        for (room in allRooms) {
            val marker = if (room in player.visitedRooms) "*" else " "
            println("$marker ${room.name}")
        }
        println()
    }

    // fight logic
    private fun fight() {
        val enemy = player.currentRoom.enemy
        if (enemy == null || !enemy.isAlive()) {
            println("There's nothing to fight here.")
            return
        }

        println("You engage the ${enemy.name}!")
        while (enemy.isAlive() && player.health > 0) {
            if ("sword" in player.inventory) {
                enemy.health -= 15
                println("You strike the ${enemy.name}! Enemy health: ${enemy.health}")
            } else {
                enemy.health -= 5
                println("You punch the ${enemy.name}. Enemy health: ${enemy.health}")
            }

            if (!enemy.isAlive()) {
                println("You defeated the ${enemy.name}! The treasure is yours. 🎉")
                println("You win!")
                System.exit(0)
            }

            // Enemy attacks back
            player.health -= enemy.damage
            println("The ${enemy.name} hits you! Your health: ${player.health}")

            if (player.health <= 0) {
                println("You were defeated... Game Over.")
                System.exit(0)
            }
        }
    }
}

fun main() {
    val game = Game()
    game.setup()
    game.start()
}
