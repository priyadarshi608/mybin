class Shape {
    public int type = 0;

    /**
     * Each shape has a corresponding bounding box that the rendering mechanism requires
     * x and y refer to the location of the top left corner of the bounding box
     * width and height are the width and height of the bounding box
     */
    protected int x, y, width, height;

    public Shape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public final double getArea() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Computed by child classes");
    }
}

class Rectangle extends Shape {
    public Rectangle(int x, int y, int width, int height) {
        this.type = 1;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getArea() {
        return width * height;
    }
}

class Square extends Shape {
    public Square(int x, int y, int width, int height) {
        this.type = 2;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getArea() {
        return this.height*this.width
    }
}

interface IAreaHelper {
    double computeArea(Shape shape);
}

class CircleAreaHelperImpl implements IAreaHelper{
    double computeArea(Shape shape) {
        if (shape instanceof Circle) {
            /* Radius is width of bounding box / 2 */
            return Math.PI * squareNum(width / 2);
        }
    }
    public double squareNum(int x) {
        return x * x;
    }
}

class Circle extends Shape {
    IArea IAreaHelper;
    public Circle(int x, int y, int width, int height) {
        this.type = 3;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setWidth(int width) {
        /* Because circle is bound by a square */
        this.width = width;
        this.height = width;
    }

    public void setHeight(int height) {
        /* Because circle is bound by a square */
        this.height = height;
        this.width = height;
    }

    public double getArea() {
        return this.areaHelper.computeArea(this);
    }
}

/**
 * The following code is the start to a RESTful API for shapes
 * There is one controller and various shape models
 */

class ShapesController {
    public ObjectRepository<Shape> shapesRepo;
    public DBConnection dbConnection;

    public ShapesController() {
        /* Database user is root with empty password */
        dbConnection = new DBConnection("root", "");

        /* Used to retrieve shape objects */
        shapesRepo = new ObjectRepository<Shape>("shapes");
    }

    /**
     * Position a shape and return its new coordinates
     *
     * @route POST /shapes/:id/position
     */
    public boolean position(Request req, Response res) {
        Shape s = shapesRepo.find(req.id);
        if (s == null) {
            System.out.println("Shape not found");
        }
        int xOffset = req.params.get("x-offset");
        int yOffset = req.params.get("y-offset");

        /* Translation */
        s.setX(s.getX() + xOffset);
        s.setY(s.getY() + yOffset);

        /* Write the change to database */
        dbConnection.update("UPDATE Shapes SET x=" + s.getX() + ",y=" + s.getY() + " WHERE id=" + req.id);

        res.setType("application/json");
        res.send("{\"x\":" + s.getX() + ",\"y\":" + s.getY() + "}");

        return true;
    }

    /**
     * Return the shape type as a string
     *
     * @route GET /shapes/:id/type
     */
    public boolean type(Request req, Response res) {
        Shape s = shapesRepo.find(req.id);

        switch (s.type) {
            case 1:
                res.send("rectangle");
                return true;
            case 2:
                res.send("square");
                return true;
            case 3:
                res.send("circle");
                return true;
            default:
                /* Invalid shape, fail */
                return false;
        }
    }
}
