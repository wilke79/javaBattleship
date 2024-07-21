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
            System.out.print("\n");
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

    public boolean placeShot(Coordinate shot) {
        if (field[shot.x - 1][shot.y - 1] == 'O') {
            field[shot.x - 1][shot.y - 1] = 'X';
            return true;
        } else {
            field[shot.x - 1][shot.y - 1] = 'M';
            return false;
        }
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
        Ship ship;
        Field field = new Field();
        field.init();
        field.print();
        List<ShipType> shipTypes = new ArrayList<>();
        shipTypes.add(new ShipType("Aircraft Carrier", 5));
        shipTypes.add(new ShipType("Battleship", 4));
        shipTypes.add(new ShipType("Submarine", 3));
        shipTypes.add(new ShipType("Cruiser", 3));
        shipTypes.add(new ShipType("Destroyer", 2));
        for(ShipType shipType: shipTypes) {
            System.out.printf("Enter the coordinates of the %s (%d cells):\n",
                    shipType.name, shipType.length);
            do {
                String input = scanner.nextLine();
                ship = new Ship(input.split(" ")[0], input.split(" ")[1]);
                if (ship.getLength() != 0 && ship.getLength() != shipType.length) {
                    System.out.printf("Error! Wrong length of the %s! Try again:\n", shipType.name);
                } else if (!field.placeShip(ship)) {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                } else if (ship.getLength() != 0) {
                    break;
                }
            } while (true);
            field.print();
        }
        System.out.println("The game starts!");
        field.printFogged();
        System.out.println("Take a shot!");
        boolean hit = false;
        do {
            String input = scanner.nextLine();
            Coordinate shot = new Coordinate(input);
            if (shot.isValid()) {
                System.out.println(shot.x);
                System.out.println(shot.y);
                hit = field.placeShot(shot);
                break;
            } else {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            }
        } while (true);
        field.printFogged();
        System.out.println(hit ? "You hit a ship!" : "You missed!");
        field.print();
    }
}
