package com.sage.graph;

import java.util.*;
import java.util.stream.Collectors;

public class GraphInputs {
    private UniverseOfDiscourse universeOfDiscourse = null;
    private SentenceMap sentenceMap = null;
    private BoundedVariableMap boundedVariableMap = null;
    private BooleanVariableMap booleanVariableMap = null;

    public GraphInputs setUD(UniverseOfDiscourse universeOfDiscourse) {
        this.universeOfDiscourse = universeOfDiscourse;
        return this;
    }

    public GraphInputs setSentenceMap(SentenceMap sentenceMap) {
        this.sentenceMap = sentenceMap;
        return this;
    }

    public GraphInputs setBoundedVariableMap(BoundedVariableMap boundedVariableMap) {
        this.boundedVariableMap = boundedVariableMap;
        return this;
    }

    public GraphInputs setBooleanVariableMap(BooleanVariableMap booleanVariableMap) {
        this.booleanVariableMap = booleanVariableMap;
        return this;
    }

    public Optional<UniverseOfDiscourse> getUD() {
        return Optional.ofNullable(universeOfDiscourse);
    }

    public Optional<SentenceMap> getSentenceMap() {
        return Optional.ofNullable(sentenceMap);
    }

    public Optional<BoundedVariableMap> getBoundedVariableMap() {
        return Optional.ofNullable(boundedVariableMap);
    }

    public Optional<BooleanVariableMap> getBooleanVariableMap() {
        return Optional.ofNullable(booleanVariableMap);
    }

    public static class UniverseOfDiscourse extends HashSet<String> {
        public void add(String... vals) {
            this.addAll(Arrays.asList(vals));
        }

        @Override
        public String toString() {
            return "{" + String.join(", ", this) + "}";
        }
    }

    public static class SentenceMap extends HashMap<String, Set<SentenceMap.TruthEntry>> {
        public static class TruthEntry {
            private final String[] vals;

            public TruthEntry(String... vals) {
                this.vals = vals;
            }

            @Override
            public boolean equals(Object o) {
                if(this == o) return true;
                if(o == null || getClass() != o.getClass()) return false;

                TruthEntry that = (TruthEntry)o;

                if(this.vals.length != that.vals.length) {
                    return false;
                }

                for(int i = 0; i < vals.length; i++) {
                    if(!this.vals[i].equals(that.vals[i])) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(vals);
            }

            @Override
            public String toString() {
                return "<" + String.join(", ", vals) + ">";
            }
        }

        @Override
        public String toString() {
            return "{" + keySet().stream().map(key -> key + ": " + get(key).toString()).collect(Collectors.joining(", ")) + "}";
        }
    }

    public static class BoundedVariableMap extends HashMap<String, String> {
        public BoundedVariableMap() {
            super();
        }

        public BoundedVariableMap(BoundedVariableMap other) {
            super(other);
        }

        @Override
        public String toString() {
            List<String> pairs = new ArrayList<>();

            for(var key : keySet()) {
                pairs.add(key + ": " + get(key));
            }

            return "{" + String.join(", ", pairs) + "}";
        }
    }

    public static class BooleanVariableMap extends HashMap<String, Boolean> {
        @Override
        public String toString() {
            List<String> pairs = new ArrayList<>();

            for(var key : keySet()) {
                pairs.add(key + ": " + get(key));
            }

            return "{" + String.join(", ", pairs) + "}";
        }
    }

    @Override
    public String toString() {
        return "Universe of Discourse: " + (universeOfDiscourse != null ? universeOfDiscourse.toString() : "NONE") + "\n" +
                "Sentence truth table: " + (sentenceMap != null ? sentenceMap.toString() : "NONE") + "\n" +
                "Boolean variable map: " + (booleanVariableMap != null ? booleanVariableMap.toString() : "NONE");
    }
}
