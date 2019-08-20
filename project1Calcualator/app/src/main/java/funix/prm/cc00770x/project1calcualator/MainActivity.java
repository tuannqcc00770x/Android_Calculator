package funix.prm.cc00770x.project1calcualator;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import static java.lang.Double.NaN;
public class MainActivity extends AppCompatActivity {
    private int[] idButton = {R.id.btnClearOne, R.id.btnClearAll, R.id.btnOpenBracket, R.id.btnCloseBracket,
            R.id.btnSqrt, R.id.btnLog, R.id.btnPower, R.id.btnPlus,
            R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnMinus,
            R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnMultiply,
            R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnDivide,
            R.id.btnZero, R.id.btnDot, R.id.btnAns, R.id.btnEqual};
    private TextView tvInput, tvOutput;
    private boolean error = false;
    private boolean endState = true;
    double ans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInput = findViewById(R.id.tvInput);
        tvOutput = findViewById(R.id.tvOutput);
        setButtonOnClickListener();
    }
    private void setButtonOnClickListener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String txtTextView = tvInput.getText().toString();
                String textEnd = checkTvInputEndsWith();
                Button button = (Button) v;
                String txtButton = button.getText().toString();
                if (error){//reset input screen
                    tvInput.setText("");
                    error = false;
                }
                switch (txtButton){
                    case "c":
                        if (txtTextView != ""){
                            if (txtTextView.endsWith("sqrt(")) {
                                tvInput.setText(txtTextView.substring(0,txtTextView.length()-5));
                            }
                            else if (txtTextView.endsWith("log10(")){
                                tvInput.setText(txtTextView.substring(0,txtTextView.length()-6));
                            }
                            else if (txtTextView.length() > 1){
                                tvInput.setText(txtTextView.substring(0,txtTextView.length()-1));
                            }
                            else {
                                tvInput.setText("");
                            }
                            endState = false;
                        }
                        break;
                    case "ac":
                        tvInput.setText("");
                        break;
                    case "(":
                        if (endState) tvInput.setText("");//reset input screen after equal
                        switch (textEnd){
                            case "+":
                            case "-":
                            case "*":
                            case "/":
                            case "sqrt":
                            case "log":
                            case "^":
                            case "(":
                            case ")":
                                tvInput.append(button.getText());
                                endState = false;
                                break;
                            case".":
                                if (convertToNumber()){
                                    tvInput.append(button.getText());
                                    endState = false;
                                }
                                break;
                            default://num from 0 to 9
                                tvInput.append(button.getText());
                                endState = false;
                                break;
                        }
                        break;
                    case ")":
                        if (endState) tvInput.setText("");//reset input screen after equal
                        if (countBrackets() > 0){//open bracket must before close bracket
                            if (isNum(textEnd) || textEnd.equals(")")) {
                                tvInput.append(button.getText());
                                endState = false;
                            }
                            else if (textEnd.equals(".")){
                                if (convertToNumber()){//3.) = 3.0)
                                    tvInput.append(button.getText());
                                    endState = false;
                                }
                            }
                        }
                        break;
                    case "sqrt":
                        if (endState) tvInput.setText("");//reset input screen after equal
                        switch (textEnd){
                            case ".":
                                if (convertToNumber()){//3.sqrt = 3.0sqrt = 3.0*sqrt
                                    tvInput.append(button.getText()+"(");
                                    endState = false;
                                }
                                break;
                            default:
                                tvInput.append(button.getText()+"(");
                                endState = false;
                                break;
                        }
                        break;
                    case "log":
                        if (endState) tvInput.setText("");//reset input screen after equal
                        switch (textEnd){
                            case ".":
                                if (convertToNumber()){//3.log = 3.0log = 3.0*log
                                    tvInput.append(button.getText()+"10(");
                                    endState = false;
                                }
                                break;
                            default:
                                tvInput.append(button.getText()+"10(");
                                endState = false;
                                break;
                        }
                        break;
                    case "+":
                    case "-":
                        if(endState) {
                            tvInput.setText("");//reset input screen after equal
                            tvInput.append(ans+""+button.getText());//recall result
                            endState = false;
                        }
                        else
                            switch (textEnd){
                                case ".":
                                    if (convertToNumber()){//3.+ = 3.0+ __ 3.- = 3.0-
                                        tvInput.append(button.getText());
                                        endState = false;
                                    }
                                    break;
                                default:
                                    tvInput.append(button.getText());
                                    endState = false;
                                    break;
                            }
                        break;
                    case "^":
                    case "*":
                    case "/":
                        if(endState) {
                            tvInput.setText("");//reset input screen after equal
                            tvInput.append(ans+""+button.getText());//recall result
                            endState = false;
                        }
                        else
                        if (isNum(textEnd) || textEnd.equals(")")) {
                            tvInput.append(button.getText());
                            endState = false;
                        }
                        else if (textEnd.equals(".")){// 3.^ = 3.0^ __ 3.* = 3.0* __ 3.^ = 3.0^
                            if (convertToNumber()){
                                tvInput.append(button.getText());
                                endState = false;
                            }
                        }
                        break;
                    case ".":
                        if (endState) tvInput.setText("");//reset input screen after equal
                        if (!checkDot()) {//not accept dot appear countinuely
                            tvInput.append(button.getText());
                            endState = false;
                        }
                        break;
                    case "ans":
                        if (endState) {
                            tvInput.setText("");//reset input screen after equal
                            tvInput.append(ans+"");//recall result
                            endState = false;
                        }
                        else if (!isNum(textEnd)||tvInput.getText().equals("")) {//recall result after operator or begin state
                            tvInput.append(ans+"");
                            endState = false;
                        }
                        break;
                    case "=":
                        if (!tvInput.getText().toString().equals("")){
                            try {
                                if (isNum(textEnd) || textEnd.equals(")")||(textEnd.equals(".")&&convertToNumber())) {//number of open and close brackets must equal together
                                    while (countBrackets()!=0){
                                        tvInput.append(")");
                                    }
                                }
                                if ((isNum(textEnd)&&countBrackets()==0)||(textEnd.equals(")")&&countBrackets()==0)
                                        ||(textEnd.equals(".")&&convertToNumber()&&countBrackets()==0)){//condition to resolve
                                    String input = tvInput.getText().toString();
                                    Expression expression = new ExpressionBuilder(input).build();
                                    double result = expression.evaluate();
                                    ans = result;// save answer
                                    if (Objects.equals(result, NaN)) throw new ArithmeticException("Math error!");
                                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                                    DecimalFormat decimalFormat = new DecimalFormat( "###,###.########" , otherSymbols );
                                    //DecimalFormat df = new DecimalFormat("0.00");//two num after dot.
                                    //tvOutput.setText(df.format(result));// print result
                                    tvOutput.setText(decimalFormat.format(result)+"");// print result
                                    endState = true;
                                }
                            } catch (ArithmeticException e) {
                                tvOutput.setText("Error, " + e.getMessage());// include catching devision by zero
                                error = true;
                            } catch (Exception e){
                                tvOutput.setText("Error, " + e.getMessage());// include cacthching NaN
                                error = true;
                            }
                        }
                        break;
                    default://num from 0 to 9
                        if (endState) tvInput.setText("");//reset input screen after equal
                        tvInput.append(button.getText());
                        endState = false;
                        break;
                }
            }
            private String checkTvInputEndsWith(){
                String txt = tvInput.getText().toString();
                if (txt.endsWith("(")) txt = "(";
                if (txt.endsWith(")")) txt = ")";
                if (txt.endsWith("sqrt")) txt = "sqrt";
                if (txt.endsWith("log")) txt = "log";
                if (txt.endsWith("^")) txt = "^";
                if (txt.endsWith("+")) txt = "+";
                if (txt.endsWith("1")) txt = "1";
                if (txt.endsWith("2")) txt = "2";
                if (txt.endsWith("3")) txt = "3";
                if (txt.endsWith("-")) txt = "-";
                if (txt.endsWith("4")) txt = "4";
                if (txt.endsWith("5")) txt = "5";
                if (txt.endsWith("6")) txt = "6";
                if (txt.endsWith("*")) txt = "*";
                if (txt.endsWith("7")) txt = "7";
                if (txt.endsWith("8")) txt = "8";
                if (txt.endsWith("9")) txt = "9";
                if (txt.endsWith("/")) txt = "/";
                if (txt.endsWith("0")) txt = "0";
                if (txt.endsWith(".")) txt = ".";
                return txt;
            }
            public boolean convertToNumber(){
                boolean check = true;
                if (tvInput.getText().toString() != ""){
                    if (tvInput.getText().toString().length() > 1){
                        if (isNum(tvInput.getText().toString().substring(tvInput.getText().toString().length()-1,tvInput.getText().toString().length()))){
                            check = false;
                        }
                    }
                }
                return check;
            }
            private boolean isNum(String c){
                boolean check;
                switch (c){
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                        check = true;
                        break;
                    default:
                        check = false;
                }
                return check;
            }
            private int countBrackets(){
                int count = 0;
                for (int i = 0; i < tvInput.getText().toString().length(); i++){
                    if (tvInput.getText().toString().length()>1){
                        if (tvInput.getText().toString().substring(i,i+1).equals("(")){
                            count++;
                        } else if (tvInput.getText().toString().substring(i,i+1).equals(")")){
                            count--;
                        }
                    } else if (tvInput.getText().toString().equals("(")) count++;
                }
                return count;
            }
            private boolean checkDot(){
                boolean check = false;
                for (int i = 0; i < tvInput.getText().toString().length(); i++) {
                    if (tvInput.getText().toString().substring(i, i + 1).equals(".")) check = true;
                    else if (!isNum(tvInput.getText().toString().substring(i, i + 1))) check = false;
                }
                return check;
            }
        };
        for (int id : idButton) {
            findViewById(id).setOnClickListener(listener);
        }
    }
}