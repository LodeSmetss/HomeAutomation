package controls;

import java.util.*;

import devices.*;

public class Condition {

    public Condition(Map<String, Object> data, Map<String, Device> allDevices) {
        List<String> termIDs = data.keySet().stream().filter(key -> key.matches("\\d+")).toList();
        termIDs.stream()
                .forEach(id -> this.terms.put(id, new Term((Map<String, Object>) data.get(id), allDevices)));
        this.representation = (String) data.get("representation");
    }

    private String representation;
    Map<String, Term> terms = new HashMap<>();

    public Boolean isValid() {
        List<String> formulaList = List.of(this.representation.split(""));
        List<String> evaluationList = new ArrayList<>();
        for (int i = 0; i < formulaList.size(); i++) {
            String chr = formulaList.get(i);
            if (chr.matches("\\d")) {
                evaluationList.add(this.terms.get(chr).isValid().toString());
            } else {
                evaluationList.add(chr);
            }
        }
        return evaluate(evaluationList);

    }


    private static Boolean evaluate(List<String> formula) {
        int brackets = 0;
        int startIndex = 0;
        boolean bracketsfound = false;

        // Look for enclosing brackets
        for (int i = 0; i < formula.size(); i++) {
            String chr = formula.get(i);
            if (chr.equals("(")) {
                brackets++;
                if (!bracketsfound) {
                    startIndex = i;
                    bracketsfound = true;
                }
            }
            if (chr.equals(")")) {
                brackets--;
            }
            if (brackets == 0 && bracketsfound) {
                List<String> firstPart = formula.subList(0, startIndex);
                List<String> toReplace = formula.subList(startIndex + 1, i);
                String replacingValue = evaluate(toReplace).toString();
                List<String> lastPart = formula.subList(i + 1, formula.size());
                List<String> evaluationList = new ArrayList<String>() {
                    {
                        addAll(firstPart);
                        add(replacingValue);
                        addAll(lastPart);
                    }
                };
                return evaluate(evaluationList);
            }
        }

        // Look for Not
        for (int i = 0; i < formula.size(); i++) {
            String chr = formula.get(i);
            if (chr.equals("_")) {
                List<String> firstPart = formula.subList(0, i);
                String toInvert = formula.get(i + 1);
                String replacingValue = ((Boolean) toInvert.equals("false")).toString();
                List<String> lastPart = formula.subList(i + 2, formula.size());
                List<String> evaluationList = new ArrayList<String>() {
                    {
                        addAll(firstPart);
                        add(replacingValue);
                        addAll(lastPart);
                    }
                };
                return evaluate(evaluationList);
            }
        }

        // Look for AND
        for (int i = 0; i < formula.size(); i++) {
            String chr = formula.get(i);
            if (chr.equals(".")) {
                List<String> firstPart = formula.subList(0, i - 1);
                List<String> toCombine = formula.subList(i - 1, i + 2);
                String replacingValue = ((Boolean) (toCombine.get(0).equals("true")
                        && toCombine.get(toCombine.size()-1).equals("true"))).toString();
                List<String> lastPart = formula.subList(i + 2, formula.size());
                List<String> evaluationList = new ArrayList<String>() {
                    {
                        addAll(firstPart);
                        add(replacingValue);
                        addAll(lastPart);
                    }
                };
                return evaluate(evaluationList);
            }
        }

        // Look for OR
        for (int i = 0; i < formula.size(); i++) {
            String chr = formula.get(i);
            if (chr.equals("+")) {
                List<String> firstPart = formula.subList(0, i - 1);
                List<String> toCombine = formula.subList(i - 1, i + 2);
                String replacingValue = ((Boolean) (toCombine.get(0).equals("true")
                        || toCombine.get(toCombine.size()-1).equals("true"))).toString();
                List<String> lastPart = formula.subList(i + 2, formula.size());
                List<String> evaluationList = new ArrayList<String>() {
                    {
                        addAll(firstPart);
                        add(replacingValue);
                        addAll(lastPart);
                    }
                };
                return evaluate(evaluationList);
            }
        }

        // Schrink formula untill one value is left
        if (formula.size() == 1) {
            return formula.get(0).equals("true");
        } else {
            return evaluate(formula);
        }

    }


}
