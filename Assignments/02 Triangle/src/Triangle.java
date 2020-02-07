import org.w3c.dom.ls.LSOutput;

class Triangle
{
    private int firstLength;
    private int secondLength;
    private int thirdLength;
    private TriangleType type;

    // Class to represent triangles
    Triangle(int first, int second, int third)
    {
        firstLength = first;
        secondLength = second;
        thirdLength = third;
        type = identifyTriangleType(first, second, third);
    }

    // Returns the (previously identified) type of this triangle
    TriangleType getType()
    {
        return type;
    }

    // Returns a printable string that describes this triangle
    public String toString()
    {
        return "(" + firstLength + "," + secondLength + "," + thirdLength + ")";
    }

    // Works out what kind of triangle this is !
    static TriangleType identifyTriangleType(long first, long second, long third)
    {
        // Sort a, b, c from smallest to largest
        long temp = third;
        if (first > second && first > third) {
            third = first;
            first = temp;
        }
        else if (second > first && second > third) {
            third = second;
            second = temp;
        }
        if (first > second) {
            temp = second;
            second = first;
            first = temp;
        }
        if (first <= 0 || second <= 0 || third <= 0) {
            return TriangleType.Illegal;
        }
        if (third > first + second) {
            return TriangleType.Impossible;
        }
        if (third == first + second) {
            return TriangleType.Flat;
        }
        if (first == second && second == third) {
            return TriangleType.Equilateral;
        }
        if (first == second || second == third || first == third)  {
            return TriangleType.Isosceles;
        }
        if (third*third == first*first + second*second) {
            return TriangleType.Right;
        }
        if (first != second && second != third && first != third)  {
            return TriangleType.Scalene;
        }
        return null;
    }
}
