import java.util.Stack;

public class Main {
    public static String convertToONP(String infix) {
        infix = infix.replaceAll("\\s","");  // Na poczatku usuwamy wszystkie spacje z dzialania
        StringBuilder onp = new StringBuilder();  // Do onp będziemy dodawać elementy na wyjście
        Stack<Character> operatorStack = new Stack<>();  // Tutaj będziemy przechowywać operatory
        for(char character : infix.toCharArray()) {
            // Sprawdzam czy symbol jest liczba jesli tak dodaje go do wyjścia
            if(character <= 57 && character >= 48){
                onp.append(character);
                // Jesli nie, to sprawdzam czy jest jednym z 4 podstawowych operatorow
            } else if ("+-*/".indexOf(character) != -1) {
                // Dopoki stos nie jest pusty, sprawdzam czy operator na stosie jest >= od nowego operatora i jesli tak przenosze go do wyjscia
                while(!operatorStack.isEmpty() && operatorPriority(character, operatorStack.peek())){
                    onp.append(operatorStack.pop());
                }
                // Dodaje operator na stos
                operatorStack.push(character);
                // Jesli mam nawias otwarty dodaje go na stos
            } else if (character == '(') {
                operatorStack.push(character);
                // Jesli dostaje nawias zamkniety to przelatuje przez stos i dodaje wszystkie operatory miedzy nawiasami
            } else if (character == ')') {
                // Dopoki stos nie jest pusty i operator nie jest nawiasem otwierajacym to dodaje operator na wyjscie
                while(!operatorStack.isEmpty() && operatorStack.peek() != '('){
                    onp.append(operatorStack.pop());
                }
                // Na koncu powinien mi zosta na gorze stosu nawias otwarty ktory musze z tego stosu usunac
                if(!operatorStack.isEmpty() && operatorStack.peek() == '('){
                    operatorStack.pop();
                }
                // Jeśli nie znajdzie nawiasu otwierajacego zwraca bład
                else {
                    throw new IllegalArgumentException("Niezrównowane nawiasy");
                }
                // Zwroci blad jesli podany zly symbol
            } else {
                throw new IllegalArgumentException("Nieznany symbol: " + character);
            }
        }
        // Na koncu dodaje wszystkie pozostale opearatory na stosie do wyjscia
        while(!operatorStack.isEmpty()){
            // Sprawdza czy na stosie w wyniku bledu nie zostaly jakies nawiasy, jesli tak wyrzuci blad
            if (operatorStack.peek() == '(' || operatorStack.peek() == ')') {
                throw new IllegalArgumentException("Niezrównowane nawiasy");
            }
            onp.append(operatorStack.pop());
        }
        return onp.toString();
    }
    public static int operatorWeight(char operator){
        // Operatorowi dodawania i odejmowania przypisuje wage 1
        if(operator == '-' || operator == '+'){
            return 1;
        }
        // Pozostalym operatorom (mnozenie, dzielenie) przypisuje wage 2
        else return 2;
    }
    public static boolean operatorPriority(char newOperator, char stackOperator){
        // Upewniam sie ze operator na stosie nie jest nawiasem
        if (stackOperator == '(' || stackOperator == ')') {
            return false;
        }
        // Zwracam prawde w przypadku kiedy operator na stosie ma wieksz lub rowna wage od nowego operatora
        return operatorWeight(stackOperator) >= operatorWeight(newOperator);
    }
    public static double countONP(String onp){
        Stack<Double> stack = new Stack<>();  // Tworzymy stos na ktory bedziemy odkladali liczby
        String[] characters = onp.split(""); // Zapis onp rozbijamy na tablice pojedynczych stringow
        for(String character : characters){
            // Sprawdzam czy symbol jest liczba jesli tak parsuje go na doubla i dodaje na stos
            if(character.charAt(0) <= 57 && character.charAt(0) >= 48){
                stack.push(Double.parseDouble(character));
                // Jesli nie to sprawdzam czy jest to operator
            } else if("+-*/".indexOf(character) != -1) {
                // Sprawdzam czy na stosie na pewno sa minimum 2 operatory
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Niewystarczająca ilość operandów");
                }
                // Wyciagamy 2 gorne liczby z stosu, i dodajemy spowrotem wynik odpowiedniego dzialania
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                double result = chooseOperator(character,operand1,operand2);
                stack.push(result);
                // Jesli nie jest to ani wybrany operator ani liczba zwraca błąd
            } else {
                throw new IllegalArgumentException("Nieznany symbol: " + character);
            }
        }
        // Na stosie powinna nam zostac tylko jedna liczba ktora bedzie wynikiem calego dzialania
        if (stack.size() == 1) {
            return stack.pop();
            // Jesli liczb jest wiecej zwraca blad
        } else {
            throw new IllegalArgumentException("Niewłaściwa ilość operandów i operatorów");
        }
    }
    public static double chooseOperator(String operator, double operand1, double operand2){
        // Sprawdzam jaki operator zostal przekazany i zwracam wynik odpowiedniego dzielenia
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                // Upewniam sie ze nie ma dzielenia przez
                if (operand2 == 0) {
                    throw new IllegalArgumentException("Dzielenie przez zero");
                }
                return operand1 / operand2;
            // Zabezpieczenie przed nieobslugiwanym operatorem
            default:
                throw new IllegalArgumentException("Nieobsługiwany operator: " + operator);
        }
    }

    public static void main(String[] args) {
        try {
            String onp = convertToONP("3+2-1+(5-2)/4");
            System.out.println(countONP(onp));
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        try {
            String onp = convertToONP("3+2-1+5-2)/4");
            System.out.println(countONP(onp));
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        try {
            String onp = convertToONP("3+2-1+(5-2)/0");
            System.out.println(countONP(onp));
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        try {
            String onp = convertToONP("Ala ma kota");
            System.out.println(countONP(onp));
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        try {
            String onp = convertToONP("3+ 2-1 +(5-2)/3 ");
            System.out.println(countONP(onp));
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }
}