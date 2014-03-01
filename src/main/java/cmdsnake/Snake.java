package cmdsnake;

import java.util.LinkedList;

public class Snake {
    private LinkedList<Pair> body = new LinkedList<Pair>();
    private Direction direction;
    private boolean crashed = false;
    private boolean eat = false;

    public Snake(int x, int y, int length, Direction direction) {
        this.direction = direction;

        for (int i = 0; i < length; i++) {
            int bodyX = x;
            int bodyY = y;

            if (direction == Direction.DOWN) {
                bodyY = bodyY - i;
            }
            if (direction == Direction.UP) {
                bodyY = bodyY + i;
            }
            if (direction == Direction.RIGHT) {
                bodyX = bodyY - i;
            }
            if (direction == Direction.LEFT)  {
                bodyX = bodyX + i;
            }

            body.add(new Pair(bodyX, bodyY));
        }
    }

    public LinkedList<Pair> getBody() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void makeStep(Direction direction) {
        if (crashed) {
            return;
        }

        this.direction = direction;

        Pair newBodyPart = null;

        if (eat) {
            newBodyPart = new Pair(body.getLast().getX(), body.getLast().getY());
        }

        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).setX(body.get(i - 1).getX());
            body.get(i).setY(body.get(i - 1).getY());
        }
        if (direction == Direction.LEFT) {
            body.get(0).setX(body.get(0).getX() - 1);
        }
        if (direction == Direction.RIGHT) {
            body.get(0).setX(body.get(0).getX() + 1);
        }
        if (direction == Direction.UP) {
            body.get(0).setY(body.get(0).getY() - 1);
        }
        if (direction == Direction.DOWN) {
            body.get(0).setY(body.get(0).getY() + 1);
        }

        if (eat) {
            body.add(newBodyPart);
            eat = false;
        }
    }

    public void makeStep() {
        makeStep(direction);
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    public boolean isEat() {
        return eat;
    }

    public void setEat(boolean eat) {
        this.eat = eat;
    }
}
