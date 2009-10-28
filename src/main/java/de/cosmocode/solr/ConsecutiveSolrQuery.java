package de.cosmocode.solr;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * <p>
 * This is a shortcut for building Solr queries. 
 * It can be directly passed to the SearchClient, which then converts it as needed.
 * </p>
 * <p>
 * <strong>Important:</strong> The SolrQuery class is not thread-safe. It should be synchronized externally.
 * </p>
 * @author olorenz
 *
 */
class ConsecutiveSolrQuery implements SolrQuery {
    
    private String dtype;
    private final Map<String, Object> requestArguments = new HashMap<String, Object>();
    
    private final StringBuilder queryArguments;
    private QueryModifier defaultModifier;
    
    private static final Logger logger = Logger.getLogger(ConsecutiveSolrQuery.class);
    
    
    
    
    /**
     * This one should be used for subqueries only.
     */
    public ConsecutiveSolrQuery () {
        this.queryArguments = new StringBuilder();
        this.setStart(0);
        this.setMax(10);
        this.setWildCarded(true);
    }
    
    
    /**
     * Constructs a new SolrQuery from the given DType (DataType).
     * @param dtype
     */
    public ConsecutiveSolrQuery (final String dtype) {
        this (dtype, true);
    }
    
    
    /**
     * Constructs a new SolrQuery from the given DType (DataType),
     * and whether all arguments should be wildCarded or not.
     * @param dtype
     * @param wildCarded
     * @param args
     */
    public ConsecutiveSolrQuery (final String dtype, final boolean wildCarded, final Object... args) {
        this.queryArguments = new StringBuilder();
        if (dtype != null) {
            addUnescapedField("dtype_s", dtype, true);
            this.dtype = dtype;
        } else {
            throw new IllegalArgumentException("SolrQuery needs a dtype");
        }
        this.setWildCarded(wildCarded);
        
        this.setStart(0);
        this.setMax(10);
        
        if (args != null && args.length > 0) {
            final QueryModifier mod = new QueryModifier(TermModifier.NONE, false, wildCarded, false);
            for (Object arg : args) {
                addArgument(arg, mod);
            }
        }
    }
    
    
    public boolean isWildCarded() {
        return defaultModifier.isWildcarded();
    }

    
    public void setWildCarded(boolean wildCarded) {
        this.defaultModifier = new QueryModifier(defaultModifier.getTermModifier(), defaultModifier.isDisjunct(), wildCarded, defaultModifier.isSplit());
    }

    
    @Override
    public String getSelectFields() {
        if (requestArguments.containsKey("fl")) {
            Object fl = requestArguments.get("fl");
            if (fl != null) return fl.toString();
        }
        return "*";
    }

    
    @Override
    public void setSelectFields(final String selectFields) {
        requestArguments.put("fl", selectFields);
    }
    
    
    @Override
    public void selectFields(String... fields) {
        setSelectFields(StringUtils.join(fields, ","));
    }

    
    @Override
    public String getSortFields() {
        if (requestArguments.containsKey("sort")) {
            Object fl = requestArguments.get("sort");
            if (fl != null) return fl.toString();
        }
        return null;
    }

    
    @Override
    public void setSortFields(String sortFields) {
        requestArguments.put("sort", sortFields);
    }

    
    @Override
    public int getMax() {
        return getRows();
    }

    
    @Override
    public void setMax(final int max) {
        setRows(max);
    }

    
    @Override
    public int getRows() {
        return (Integer)requestArguments.get("rows");
    }

    
    @Override
    public void setRows(final int rows) {
        final int absMax = (rows < 0) ? Math.abs(rows) : rows;
        requestArguments.put("rows", absMax);
    }

    
    @Override
    public int getStart() {
        return (Integer)requestArguments.get("start");
    }

    
    @Override
    public void setStart(final int start) {
        if (start < 0) throw new IllegalArgumentException ("start must be a non-negative integer");
        requestArguments.put("start", start);
    }
    
    
    @Override
    public void setRequestArgument(final String name, final String value) {
        if ("q".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument q cannot be directly assigned, use the addArgument-methods");
        if ("start".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument start cannot be directly assigned, use setStart(int)");
        if ("rows".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument rows cannot be directly assigned, use setRows(int)");
        requestArguments.put(name, value);
    }
    
    
    @Override
    @SuppressWarnings("unchecked")
    public void addFacetField (final String facetFieldName) {
        if (facetFieldName != null) {
            Set<String> facetFields = new HashSet<String>();
            if (!requestArguments.containsKey("facet.field")) {
                facetFields = new HashSet<String>();
                requestArguments.put("facet.field", facetFields);
                requestArguments.put("facet", true);
            } else {
                try {
                    facetFields = (Set<String>)requestArguments.get("facet.field");
                } catch (ClassCastException e) { logger.warn("Cannot get facet.field", e); }
            }
            facetFields.add(facetFieldName);
        }
    }
    
    
    @Override
    public void addFacetFields (final String... facetFields) {
        if (facetFields == null) return;
        
        for (final String facetField : facetFields) {
            this.addFacetField(facetField);
        }
    }
    
    
    @Override
    public String getQuery() {
        return this.queryArguments.toString();
    }
    
    
    /**
     * @param logRequestArgs if true, then SolrQuery logs the returned map in log4j. Otherwise not.
     * @return the request arguments in a new HashMap 
     * (no modifications on this SolrQuery possible)
     */
    protected Map<String, Object> getRequestArguments(final boolean logRequestArgs) {
     // put requests into a separate map to prevent modification and to add the query
        final Map<String, Object> requestArgs = new HashMap<String, Object>(this.requestArguments);
        requestArgs.put("q", this.queryArguments);
        if (logRequestArgs && logger.isDebugEnabled()) logger.debug(requestArgs.toString());
        return requestArgs;
    }
    
    
    @Override
    public Map<String, Object> getRequestArguments() {
        return this.getRequestArguments(true);
    }
    
    
    @Override
    public Set<Map.Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments(true).entrySet();
    }
    

    @Override
    public String getDtype() {
        return dtype;
    }
    
    
    
    //---------------------------
    //     addArgument-methods
    //---------------------------
    
    // shortcuts
    public ConsecutiveSolrQuery and() {
        return this.addUnescaped("AND", false);
    }
    public ConsecutiveSolrQuery or() {
        return this.addUnescaped("OR", false);
    }
    public ConsecutiveSolrQuery not() {
        return this.addUnescaped("NOT", false);
    }
    

    @Override
    public ConsecutiveSolrQuery addFuzzyArgument (final String value, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addFuzzyArgument(value, mod, defaultFuzzyness);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFuzzyArgument (final String value, boolean mandatory, double fuzzyness) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addFuzzyArgument(value, mod, fuzzyness);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFuzzyArgument (final String value, final QueryModifier modifier, final double fuzzyness) {
        if (fuzzyness < 0.0 || fuzzyness >= 1.0)
            throw new IllegalArgumentException("fuzzyness must be greater than equals 0 and less than 1 (i.e. 0 <= fuzzyness < 1)");
        
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            queryArguments.append("(")
                .append(escapeQuotes(escapeInput(value, true)))
                .append("~").append(fuzzyness)
            .append(") ");
        }
        
        return this;
    }

    
    @Override
    public ConsecutiveSolrQuery addArgument (final String value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgument(value, mod);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addArgument (final String value, final QueryModifier modifier) {
        // TODO: implement isSplit() of QueryModifier
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            if (modifier.isWildcarded()) {
                // search for input wildcarded (wildcard is appended at the end). 
                // the original input is added, too, because e.g. adidas* doesn't match "adidas" on text-fields
                queryArguments.append("(")
                    .append("\"").append(escapeQuotes(value)).append("\"^2")
                    .append(" ").append(escapeQuotes(escapeInput(value, true))).append("*")
                .append(")");
            } else {
                queryArguments.append(escapeQuotes(escapeInput(value, true)));
            }
            queryArguments.append(" ");
        }
        
        return this;
    }
    
    
    // add collection and array
    
    @Override
    public ConsecutiveSolrQuery addArgument (final Collection<?> value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgumentAsCollection(value, mod);
    }
    
    
    @Override
    public <K> ConsecutiveSolrQuery addArgument (final K[] values, final QueryModifier modifiers) {
        return this.addArgumentAsArray(values, modifiers);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addArgumentAsCollection (final Collection<?> values, final QueryModifier modifier) {
        if (values != null && values.size() > 0) {
            // start
            queryArguments.append("(");

            // add items
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (Object val : values) {
                addArgument(val, valueModifier);
            }

            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public <K> ConsecutiveSolrQuery addArgumentAsArray (final K[] values, final QueryModifier modifier) {
        if (values != null && values.length > 0) {
            // start
            queryArguments.append("(");
            
            // add items
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (K val : values) {
                addArgument(val, valueModifier);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addArgumentAsArray (Object values, final QueryModifier modifier) {
        if (values != null && values.getClass().isArray() && Array.getLength(values) > 0) {
            final int arrayLength = Array.getLength(values);
            
            // start
            queryArguments.append("(");
            
            // add all items
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (int i = 0; i < arrayLength; i++) {
                addArgument(Array.get(values, i), valueModifier);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addArgument (final Object value, final QueryModifier modifiers) {
        if (value == null) return this;
        
        if (value instanceof String) {
            return this.addArgument(value.toString(), modifiers);
        } else if (value instanceof Collection<?>) {
            return this.addArgumentAsCollection((Collection<?>)value, modifiers);
        } else if (value.getClass().isArray()) { 
            return this.addArgumentAsArray(value, modifiers);
        } else if (value instanceof ConsecutiveSolrQuery) {
            return this.addSubquery((ConsecutiveSolrQuery)value, modifiers);
        } else {
            return this.addArgument(value.toString(), modifiers);
        }
    }
    
    
    
    @Override
    public ConsecutiveSolrQuery addSubquery (final SolrQuery value, final boolean mandatory) {
        if (value != null) {
            if (mandatory) queryArguments.append("+");
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addSubquery (final SolrQuery value, final QueryModifier modifiers) {
        if (value != null) {
            queryArguments.append(modifiers.getTermPrefix());
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    
    //---------------------------
    //     addUnescaped-methods
    //---------------------------
    
    
    @Override
    public ConsecutiveSolrQuery addUnescapedField (final String key, final CharSequence value, final boolean mandatory) {
        if (key == null || value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(key).append(":").append(value).append(" ");
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addUnescaped (final CharSequence value, final boolean mandatory) {
        if (value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(value).append(" ");
        
        return this;
    }
    
    
    
    //---------------------------
    //     addField-methods
    //---------------------------
    
    
    @Override
    public ConsecutiveSolrQuery addField (final String key, final Object value, final QueryModifier modifiers) {
        if (value instanceof Collection<?>) {
            return this.addFieldAsCollection(key, (Collection<?>)value, modifiers);
        } else if (value instanceof String) {
            return this.addField(key, (String)value, modifiers);
        } else if (value != null && value.getClass().isArray()) {
            return this.addFieldAsArray(key, value, modifiers);
        }
        
        // default implementation: no check possible
        if (StringUtils.isNotBlank(key) && value != null) {
            startField(key, modifiers);
            addArgument(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addField (final String key, final String value, final boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addField(key, value, mod);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addField (String key, String value, boolean mandatoryKey, double boostFactor) {
        return this.addField(key, value, mandatoryKey).addBoost(boostFactor);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addField (final String key, final String value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, modifiers);
            this.addArgument(value, modifiers);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addArgument(token, modifiers);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey) {
        return this.addFuzzyField(key, value, mandatoryKey, defaultFuzzyness);
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey, final double fuzzyness) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, mandatoryKey);
            this.addFuzzyArgument(value, false, fuzzyness);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addFuzzyArgument(token, false, fuzzyness);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, mandatoryKey);
            addArgument(value, mandatoryValue);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue, double boostFactor) {
        return this.addField(key, mandatoryKey, value, mandatoryValue).addBoost(boostFactor);
    }
    
    
    @Override
    public <K> ConsecutiveSolrQuery addField (final String key, final K[] value, final QueryModifier modifiers) {
        return this.addFieldAsArray(key, value, modifiers);
    }

    
    @Override
    public ConsecutiveSolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, modifiers);
            addArgumentAsCollection(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers, final double boost) {
        return this.addFieldAsCollection(key, value, modifiers).addBoost(boost);
    }
    
    
    @Override
    public <K> ConsecutiveSolrQuery addFieldAsArray (final String key, final K[] value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.length > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addFieldAsArray (final String key, final Object value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.getClass().isArray() && Array.getLength(value) > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    

    //---------------------------
    //  helper methods
    //---------------------------
    
    @Override
    public ConsecutiveSolrQuery startField (final String fieldName, final boolean mandatory) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery startField (final String fieldName, final QueryModifier modifier) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        queryArguments.append(modifier.getTermPrefix());
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery endField () {
        if (queryArguments.charAt(queryArguments.length()-1) == '(') {
            // add an empty string, if the field was ended right after it was started
            queryArguments.append("\"\"");
        }
        queryArguments.append(") ");
        
        return this;
    }
    
    
    @Override
    public ConsecutiveSolrQuery addBoost (final double boostFactor) {
        if (boostFactor <= 0.0 || boostFactor >= 10000000.0)
            throw new IllegalArgumentException("boostFactor must be greater than 0 and less than 10.000.000 (10 Mio.)");
        
        if (boostFactor != 1.0) { // optimization: only add boost factor if != 1
            final double rounded = ((int)(boostFactor * 100.0)) / 100.0;
            this.queryArguments.append("^").append(rounded).append(" ");
        }

        return this;
    }
    
    
    @Override
    public String toString() {
        return getRequestArguments(false).toString();
    }

    
    //---------------------------
    //   public helper methods
    //---------------------------
    
    // escape +,\,&,|,!,(,),{,},[,],^,~,?,*,: and blanks  with "\"
    public static final Pattern escapePattern         = Pattern.compile("[\\Q+\\&|!(){}[]^~?*:; \\E]");
    // escape +,\,&,|,!,(,),{,},[,],^,~,?,*,: with "\"
    public static final Pattern escapeNoBlanksPattern = Pattern.compile("[\\Q+\\&|!(){}[]^~?*:;\\E]");
    // remove -
    public static final Pattern removePattern         = Pattern.compile("[\\-]");
    // remove - and blanks
    public static final Pattern removeBlanksPattern   = Pattern.compile("[\\- ]");
    
    public static final Pattern quotesPattern         = Pattern.compile("[\"]");
    
    
    /**
     * Escapes quotes (") in a given input (" => \")
     * @param input
     * @return
     */
    public static String escapeQuotes (final String input) {
        if (input == null) return "";
        return quotesPattern.matcher(input).replaceAll("\\\\$0");
    }
    
    
    /**
     * Removes quotes (") from a given input
     * @param input
     * @return
     */
    public static String removeQuotes (final String input) {
        if (input == null) return "";
        return quotesPattern.matcher(input).replaceAll("");
    }
    
    
    /**
     * Escapes special chars for solr.<br>
     * Special chars are: +,\,&,|,!,(,),{,},[,],^,~,?,*,:.<br>
     * If `escapeBlanks` is true, then blanks are escaped, otherwise they are removed
     * They are escaped with "\".<br>
     * <br>
     * This function was taken (with heavy modifications) from
     * http://www.javalobby.org/java/forums/t86124.html
     * @param input the input to escape
     * @return the input, escaped for solr
     */
    public static String escapeInput (final String input, final boolean escapeBlanks) {
        if (input == null) return "";
        return 
        (escapeBlanks ? escapePattern : escapeNoBlanksPattern).matcher(
            (escapeBlanks ? removePattern : removeBlanksPattern).matcher(
                input
            ).replaceAll("")
        ).replaceAll("\\\\$0");
    }


    // TODO: move into (new) AbstractLuceneQueryBuilder and implement these methods
    
    @Override
    public SolrQuery addFieldAsCollection(String key, Collection<?> value) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addFuzzyArgument(String value) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addFuzzyArgument(String value, QueryModifier modifiers) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addFuzzyField(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addFuzzyField(String key, String value, QueryModifier mod) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addFuzzyField(String key, String value, QueryModifier mod,
            double fuzzyness) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SolrQuery addSubquery(SolrQuery value) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public QueryModifier getDefaultQueryModifier() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setDefaultQueryModifier(QueryModifier mod) {
        // TODO Auto-generated method stub
        
    }
    

}
