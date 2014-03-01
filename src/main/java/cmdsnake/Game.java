package cmdsnake;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {

    Terminal terminal;
    Screen screen;
    ScreenWriter writer;
    private List<Snake> snakes = new LinkedList<Snake>();
    private List<LinkedList<Direction>> directions = new LinkedList<LinkedList<Direction>>();
    private Key[][] keys = {
            {new Key(Key.Kind.ArrowLeft), new Key(Key.Kind.ArrowUp), new Key(Key.Kind.ArrowRight), new Key(Key.Kind.ArrowDown)},
            {new Key('a'), new Key('w'), new Key('d'), new Key('s')}
    };

    private Pair apple;

    public Game() {
        terminal =  TerminalFacade.createTerminal();

        screen = new Screen(terminal);

        screen.setCursorPosition(null); // Hack to hide command line cursor
        screen.startScreen();

        screen.refresh();

        writer = new ScreenWriter(screen);


        Snake snake1 = new Snake(70, 20, 3, Direction.UP);
        Snake snake2 = new Snake(10, 20, 3, Direction.UP);
        snakes.add(snake1);
        snakes.add(snake2);

        generateApple();

        for (int i = 0; i < snakes.size(); i++) {
            LinkedList<Direction> keyQueue = new LinkedList<Direction>();
            directions.add(keyQueue);
        }
    }

    public void start() {

        drawScreen();

        long stepStartMillis = 0;

        while (true) {

            Key key = terminal.readInput();

            if (key != null) {
                Direction direction = getDirectionFromKey(key);

                if (direction != null) {

                    int snakeNumber = getSnakeNumberFromKey(key);
                    if (directions.get(snakeNumber).size() == 0) {
                        if (snakes.get(snakeNumber).getDirection() != getOppositeDirection(direction)) {
                            directions.get(snakeNumber).add(direction);
                        }
                    } else if (directions.get(snakeNumber).getLast() != getOppositeDirection(direction)) {
                        directions.get(snakeNumber).add(direction);
                    }
                } else if (Character.valueOf(' ').equals(key.getCharacter())) {
                    break;
                }
            }

            long currentMillis = System.currentTimeMillis();
            if (currentMillis - stepStartMillis > 250) {
                stepStartMillis = currentMillis;

                for (int i = 0; i < snakes.size(); i++) {
                    if (directions.get(i).size() > 0) {
                        snakes.get(i).makeStep(directions.get(i).remove());
                    } else {
                        snakes.get(i).makeStep();
                    }
                }

                for (Snake snake : snakes) {
                    if (snake.getBody().getFirst().equals(apple)) {
                        snake.setEat(true);
                        generateApple();
                    }
                }

                drawScreen();

                for (Snake snake : snakes) {
                    Pair snakeHead = snake.getBody().getFirst();

                    for (Snake snake2 : snakes) {
                        for (int i = snake == snake2 ? 1 : 0; i < snake2.getBody().size(); i++) {
                            if (snake2.getBody().get(i).equals(snakeHead)) {
                                snake.setCrashed(true);
                            }
                        }
                    }

                    if (snakeHead.getX() == 0 || snakeHead.getY() == 0 || snakeHead.getX() == 79 || snakeHead.getY() == 23) {
                        snake.setCrashed(true);
                    }
                }

                int snakesAlive = 0;
                for (Snake snake : snakes) {
                    if (!snake.isCrashed()) {
                        snakesAlive++;
                    }
                }

                if (snakesAlive < 2) {


                    writer.drawString(20, 10, "##############################");
                    if (snakesAlive == 0) {
                        writer.drawString(20, 11, "#     Game has no winner     #");
                    } else {
                        writer.drawString(20, 11, "#         Game over!         #");
                    }
                    writer.drawString(20, 12, "##############################");
                    break;
                }


            }


        }

        screen.refresh();

        while (true) {
            Key key = terminal.readInput();
            if (key != null) {
                if (Character.valueOf(' ').equals(key.getCharacter())) {
                    break;
                }
            }
        }

        screen.stopScreen();
    }

    private void drawScreen() {
        screen.clear();

        writer.drawString(0, 0, "################################################################################");
        for (int i = 1; i < 23; i++) {
            writer.drawString(0, i, "#");
            writer.drawString(79, i, "#");
        }
        writer.drawString(0, 23, "################################################################################");


        for (Snake snake : snakes) {
            for (int i = 1; i < snake.getBody().size(); i++) {
                writer.drawString(snake.getBody().get(i).getX(), snake.getBody().get(i).getY(), "X");
            }
        }

        for (Snake snake : snakes) {
            writer.drawString(snake.getBody().get(0).getX(), snake.getBody().get(0).getY(), "@");
        }

        writer.drawString(apple.getX(), apple.getY(), "A");

        screen.refresh();
    }

    private Direction getDirectionFromKey(Key key) {
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < keys[0].length; j++) {
                if (keys[i][j].equals(key)) {
                    return Direction.values()[j];
                }
            }
        }
        return null;
    }

    private int getSnakeNumberFromKey(Key key) {
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < keys[0].length; j++) {
                if (keys[i][j].equals(key)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private Direction getOppositeDirection(Direction direction) {
        if (direction == Direction.LEFT) return Direction.RIGHT;
        if (direction == Direction.RIGHT) return Direction.LEFT;
        if (direction == Direction.UP) return Direction.DOWN;
        if (direction == Direction.DOWN) return Direction.UP;

        throw new IllegalArgumentException("Something went terribly wrong in getOppositeDirection function. Passed direction: " + direction);
    }

    private void generateApple() {
        Random random = new Random();
        Pair pair;

        do {
            pair = new Pair(random.nextInt(80), random.nextInt(24));
        } while (isOccupied(pair));

        apple = pair;
    }

    private boolean isOccupied(Pair pair) {
        if (pair.getX() == 0 || pair.getY() == 0 || pair.getX() == 79 || pair.getY() == 23) {
            return true;
        }

        for (Snake snake : snakes) {
            if (snake.getBody().contains(pair)) {
                return true;
            }
        }

        return false;
    }

}
