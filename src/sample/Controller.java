package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;


public class Controller {

    //вариации символов
    private final String OPERATORS = "+-*/";
    private final String SEPARATOR = ",";
    private final String VARIABLE = "var";

    //стек для операций
    private Stack<String> stackOperations = new Stack<String>();
    //cтек для реализации польской нотации
    private Stack<String> stackRPN = new Stack<String>();
    // стек для подсчета результата
    private Stack<String> stackResult = new Stack<String>();

    @FXML
    private TextField outputStr;
    @FXML
    private Label resultLabel;

    //проверка токена на число
    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
        } catch (Exception e) {
            if (token.equals(VARIABLE)) {
                return true;
            }
            return false;
        }
        return true;
    }

    //проверка на разделитель (нецелое число)
    private boolean isSeparator(String token) {
        return token.equals(SEPARATOR);
    }
    //проверка на открытие скобки
    private boolean isOpenBracket(String token) {
        return token.equals("(");
    }
    //проверка на закрытие скобки
    private boolean isCloseBracket(String token) {
        return token.equals(")");
    }
    //проверка на оператор
    private boolean isOperator(String token) {
        return OPERATORS.contains(token);
    }
    //проверка на знак минус перед числом
    private byte getPrecedence(String token) {
        if (token.equals("+") || token.equals("-")) {
            return 1;
        }
        return 2;
    }

    //запускается при создании окна, здесь запрещаем ввод ненужных символов
    public void initialize() {
        outputStr.addEventFilter(KeyEvent.KEY_TYPED, numFilter());
    }

    //запрещаем ввод ненужных символов
    public static EventHandler<KeyEvent> numFilter() {
        EventHandler<KeyEvent> aux = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
                if (!"0123456789-+/*.()".contains(keyEvent.getCharacter())) {
                    keyEvent.consume();
                }
            }
        };
        return aux;
    }

    //проверка на число
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    //слушаем нажатые кнопки и записываем их значение в строку
    public void printOnString(javafx.event.ActionEvent actionEvent) {

        Object source = actionEvent.getSource();  //получаем источник и записываем его в Object
        if (!(source instanceof Button)) {          //проверяем, является ли текущий объект кнопкой, если нажата не кнопка - выходим из метода
            return;
        }
        Button clickedButton = (Button) source;
        outputStr.setText(outputStr.getText() + clickedButton.getText());       //записываем полученный текст к уже имеющемуся
    }
    // метод очистки строки
    public void deleteText(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        outputStr.clear();

    }


    public void calculate(ActionEvent actionEvent) throws Exception {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        String result = outputStr.getText();

        //вызываем метод перевода строки в обратную польскую нотацию
        parsingNotation(result);
        System.out.println(result);

    }


    public void  parsingNotation(String initialString ) throws Exception  {

        stackOperations.clear();
        stackRPN.clear();
        double var1, var2,res = 0;
        try {

            //приводим строку в нужный вид
            initialString = initialString.replace(" ", "").replace("(-", "(0-")
                    .replace(",-", ",0-");
            if (initialString.charAt(0) == '-') {
                initialString = "0" + initialString;
            }
            // разбиваем строку на токены
            StringTokenizer stringTokenizer = new StringTokenizer(initialString,
                    OPERATORS + SEPARATOR + "()", true);

            // проверяем по циклу каждый токен по алгоритму сортировочной станции
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                if (isSeparator(token)) {
                    while (!stackOperations.empty()
                            && !isOpenBracket(stackOperations.lastElement())) {
                        stackRPN.push(stackOperations.pop());
                    }
                } else if (isOpenBracket(token)) {
                    stackOperations.push(token);
                } else if (isCloseBracket(token)) {
                    while (!stackOperations.empty()
                            && !isOpenBracket(stackOperations.lastElement())) {
                        stackRPN.push(stackOperations.pop());
                    }
                    stackOperations.pop();
                    if (!stackOperations.empty()) {
                        stackRPN.push(stackOperations.pop());
                    }
                } else if (isNumber(token)) {

                    stackRPN.push(token);

                } else if (isOperator(token)) {
                    while (!stackOperations.empty()
                            && isOperator(stackOperations.lastElement())
                            && getPrecedence(token) <= getPrecedence(stackOperations
                            .lastElement())) {
                        stackRPN.push(stackOperations.pop());
                    }
                    stackOperations.push(token);
                }
            }
        } catch (EmptyStackException e){
            Alert alert2 = new Alert(Alert.AlertType.WARNING);
            alert2.setTitle("Warning Dialog");
            alert2.setHeaderText("Проверьте правильность введенных данных!");
            alert2.showAndWait();
        }
        while (!stackOperations.empty()) {
            stackRPN.push(stackOperations.pop());
        }

        Collections.reverse(stackRPN);

        //бежим по перевернотуму стеку, который уже является польской нотацией первичного выражения

        while (!stackRPN.empty()) {
            if (isNumeric(stackRPN.lastElement())) {
                stackResult .push(stackRPN.pop());
            } else {
                var2=Double.parseDouble (stackResult .pop());
                var1=Double.parseDouble (stackResult .pop());
                switch (stackRPN.pop()){
                    case "+": res = var1 + var2; break;
                    case "-": res = var1 - var2; break;
                    case "*": res = var1 * var2; break;
                    case "/": res = var1 / var2; break;
                    default:
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning Dialog");
                        alert.setHeaderText("Ошибка при вычислении!");
                        alert.showAndWait();
                }
                stackResult .push(String.valueOf(res));
            }

        }
        initialString=stackResult .pop();
        resultLabel.setText(initialString);
    }

    //реализация backspace
    public void deleteLast(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        outputStr.setText(outputStr.getText().substring(0,outputStr.getLength()-1));
    }
}
