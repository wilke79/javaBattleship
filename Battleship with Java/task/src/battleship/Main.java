package battleship;

import java.util.*;

class Field {
    final int NUM_COLS = 10;
    final int NUM_ROWS = 10;
    char[][] field;

    public void init() {
        this.field = new char[NUM_COLS][NUM_ROWS];
        for (int i = 0; i < NUM_COLS; ++i) {
            for (int j = 0; j < NUM_ROWS; ++j) {
                this.field[j][i] = '~';
            }
        }
    }

    public void print() {
        for (int i = 0; i <= NUM_COLS; ++i) {
            for (int j = 0; j <= NUM_ROWS; ++j) {
                    System.out.print(i == 0 ? j == 0 ? " " : j :
                            j == 0 ? Character.toString(64 + i) : this.field[j - 1][i - 1]);
                    System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    public void printFogged() {
        for (int i = 0; i <= NUM_COLS; ++i) {
            for (int j = 0; j <= NUM_ROWS; ++j) {
                System.out.print(i == 0 ? j == 0 ? " " : j :
                        j == 0 ? Character.toString(64 + i) :
                                this.field[j - 1][i - 1] == 'O' ? '~' : this.field[j - 1][i - 1]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    private boolean isClear(Ship ship) {
        for (Coordinate part: ship.getParts()) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    try {
                        if (field[part.x - i][part.y - i] == 'O') {
                            return false;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                }
            }
        }
        return true;
    }

    public boolean placeShip(Ship ship) {
        if (!isClear(ship)) {
            return false;
        }
        for (Coordinate part: ship.getParts()) {
            field[part.x - 1][part.y - 1] = 'O';
        }
        return true;
    }

    public Ship placeShot(Coordinate shot, List<Ship> ships) {
        if (field[shot.x - 1][shot.y - 1] == 'O' || field[shot.x - 1][shot.y - 1] == 'X') {
            field[shot.x - 1][shot.y - 1] = 'X';
            for (Ship ship: ships) {
                for (Coordinate part: ship.getParts()) {
                    if (part.x == shot.x && part.y == shot.y) {
                        ship.setHitPart(shot);
                        return ship;
                    }
                }
            }
        } else {
            field[shot.x - 1][shot.y - 1] = 'M';
        }
        return null;
    }

    public boolean allShipsSunk() {
        for (int i = 0; i < NUM_COLS; ++i) {
            for (int j = 0; j < NUM_ROWS; ++j) {
                if (field[i][j] == 'O') {
                    return false;
                }
            }
        }
        return true;
    }
}

class Coordinate {
    int x;
    int y;

    public Coordinate(int x , int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(String field) {
        if (field.isEmpty()) {
            this.x = -1;
            this.y = -1;
        } else {
            try {
                this.x = Integer.parseInt(field.substring(1));
                this.y = field.charAt(0) - 64;
            } catch (Exception e) {
                System.out.println("Error!");
            }
        }
    }

    public boolean isValid() {
        return this.x >= 1 && this.x <= 10 && this.y >= 1 && this.y <= 10;
    }
}

class Ship {
    final Coordinate start;
    final Coordinate end;
    final int length;
    final List<Coordinate> parts = new ArrayList<>();
    final List<Coordinate> hitParts = new ArrayList<>();

    public Ship(String start, String end) {
        Coordinate startCoordinate = new Coordinate(start);
        Coordinate endCoordinate = new Coordinate(end);
        if (!(startCoordinate.isValid() && endCoordinate.isValid())) {
            this.start = new Coordinate("");
            this.end = new Coordinate("");
            this.length = 0;
            System.out.println("Error! Wrong ship location! Try again!");
        } else if (startCoordinate.x == endCoordinate.x) {
            this.start = startCoordinate;
            this.end = endCoordinate;
            this.length = Math.abs(startCoordinate.y - endCoordinate.y) + 1;
            int i = 0;
            while (Math.abs(i) < length) {
                Coordinate part = new Coordinate(startCoordinate.x, startCoordinate.y + i);
                this.parts.add(part);
                i += startCoordinate.y > endCoordinate.y ? -1 : 1;
            }
        } else if (startCoordinate.y == endCoordinate.y) {
            this.start = startCoordinate;
            this.end = endCoordinate;
            this.length =  Math.abs(startCoordinate.x - endCoordinate.x) + 1;
            int i = 0;
            while (Math.abs(i) < length) {
                Coordinate part = new Coordinate(startCoordinate.x + i, startCoordinate.y);
                this.parts.add(part);
                i += startCoordinate.x > endCoordinate.x ? -1 : 1;
            }
        } else {
            this.start = new Coordinate("");
            this.end = new Coordinate("");
            this.length = 0;
            System.out.println("Error! Wrong ship location! Try again!");
        }
    }

    public int getLength() {
        return this.length;
    }

    public List<Coordinate> getParts() {
        return parts;
    }

    public void setHitPart(Coordinate part) {
        this.hitParts.add(part);
    }

    public boolean isShipSunk() {
        return hitParts.size() == this.length;
    }
}

class ShipType {
    String name;
    int length;

    ShipType(String name, int length) {
        this.name = name;
        this.length = length;
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<ShipType> shipTypes = new ArrayList<>();
        shipTypes.add(new ShipType("Aircraft Carrier", 5));
        shipTypes.add(new ShipType("Battleship", 4));
        shipTypes.add(new ShipType("Submarine", 3));
        shipTypes.add(new ShipType("Cruiser", 3));
        shipTypes.add(new ShipType("Destroyer", 2));
        String[] players = {"Player 1", "Player 2"};
        Field[] field = {new Field(), new Field()};
        Ship[][] ships = new Ship[2][shipTypes.size()];
        for (int i = 0; i < players.length; i++) {
            System.out.printf("%s, place your ships on the game field\n", players[i]);
            field[i].init();
            field[i].print();
            for (int j = 0; j < shipTypes.size(); j++) {
                Ship ship;
                System.out.printf("Enter the coordinates of the %s (%d cells):\n",
                        shipTypes.get(j).name, shipTypes.get(j).length);
                do {
                    String input = scanner.nextLine();
                    ship = new Ship(input.split(" ")[0], input.split(" ")[1]);
                    if (ship.getLength() != 0 && ship.getLength() != shipTypes.get(j).length) {
                        System.out.printf("Error! Wrong length of the %s! Try again:\n", shipTypes.get(j).name);
                    } else if (!field[i].placeShip(ship)) {
                        System.out.println("Error! You placed it too close to another one. Try again:");
                    } else if (ship.getLength() != 0) {
                        ships[i][j] = ship;
                        break;
                    }
                } while (true);
                field[i].print();
            }
            System.out.println("Press Enter and pass the move to another player");
            scanner.nextLine();
        }
        System.out.println("The game starts!");
        int activePlayer = 0;
        do {
            field[(activePlayer + 1) % 2].printFogged();
            System.out.println("----------------------------");
            field[activePlayer].print();
            System.out.printf("%s, it's your turn:", players[activePlayer]);
            Ship hitShip;
            do {
                String input = scanner.nextLine();
                Coordinate shot = new Coordinate(input);
                if (shot.isValid()) {
                    hitShip = field[(activePlayer + 1) % 2].placeShot(shot,
                            Arrays.stream(ships[(activePlayer + 1) % 2]).toList());
                    activePlayer = (activePlayer + 1) % 2;
                    break;
                } else {
                    System.out.println("Error! You entered the wrong coordinates! Try again:");
                }
            } while (true);
            if (field[0].allShipsSunk() || field[1].allShipsSunk()) {
                System.out.println("You sank the last ship. You won. Congratulations!");
                System.exit(0);
            }
            System.out.println(hitShip == null ? "You missed!" : hitShip.isShipSunk() ? "You sank a ship!" :
                            "You hit a ship!");
            System.out.println("Press Enter and pass the move to another player");
            scanner.nextLine();
        } while (true);
    }
}
