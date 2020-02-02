import java.io.*;

// Class to convert unit marks to grades
class GradeConverter
{
    // Converts a numerical mark (0 to 100) into a textual grade
    // Returns "Invalid" if the number is invalid
    String convertMarkToGrade(int mark)
    {
        if (mark >= 0) {
            if (mark < 50) {
                return "Fail";
            } else if (mark < 60) {
                return "Pass";
            } else if (mark < 70) {
                return "Merit";
            } else if (mark <= 100) {
                return "Distinction";
            }
        }
        return "Invalid";
    }

    // Reads a mark from a String and returns the mark as an int (0 to 100)
    // Returns -1 if the string is invalid
    int convertStringToMark(String text)
    {
        if (text == null) {
            return -1;
        }
        if (text.length() == 0) {
            return -1;
        }
        text = text.replace("%", "");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (convertCharToInt(c) == -1) {
                if (c != '.') {
                    return -1;
                }
            }
        }
        float mark = Float.parseFloat(text);
        if (mark > 100) {
            return -1;
        }
        if (text.charAt(0) == '0' && mark > 0) {
            return -1;
        }
        int result = Math.round(mark);
        return result;
    }

    // Convert a single character to an int (0 to 9)
    // Returns -1 if char is not numerical
    int convertCharToInt(char c)
    {
        if (c < '0' || c > '9') {
            return -1;
        }
        return Character.getNumericValue(c);
    }

    public static void main(String[] args) throws IOException
    {
        GradeConverter converter = new GradeConverter();
        while(true) {
            System.out.print("Please enter your mark: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            int mark = converter.convertStringToMark(input);
            String grade = converter.convertMarkToGrade(mark);
            System.out.println("A mark of " + input + " is " + grade);
        }
    }
}