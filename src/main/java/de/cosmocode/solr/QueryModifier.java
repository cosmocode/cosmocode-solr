package de.cosmocode.solr;

import de.cosmocode.patterns.Immutable;

/**
 * This is an immutable class that affects the addTerm and addField methods of SolrQuery.
 * Documentation for the input types is explained in the Constructors.
 * 
 * @author olorenz
 *
 */
@Immutable
public final class QueryModifier {
    
    // TODO: Javadoc
    
    private final TermModifier termModifier;
    
    private final boolean disjunct;
    
    private final boolean wildcarded;
    
    private final boolean split;
    
    
    /**
     * Default constructor.
     * Sets the modifier to NONE, and wildcarded, split and disjunct to false. 
     * 
     * 
     * A call to addField would look like this:<br>
     * <i>addField("test", new String[] {"test1", "test2"}, new QueryModifiers()) => test:(test1 test2)
     * 
     * 
     * And a call to addTerm would look like this:<br>
     * <i>addTerm("test", new QueryModifiers()) => test
     * 
     */
    public QueryModifier() {
        this.termModifier = TermModifier.NONE;
        this.disjunct = false;
        this.wildcarded = false;
        this.split = false;
    }
    
    
    /**
     * This constructor sets modifier to the given modifier
     * and wildcarded, split and disjunct to false.
     * 
     * 
     * A call to addField would for example look like this:<br>
     * <pre>
     * final QueryModifier mod = new QueryModifier({@link TermModifier#REQUIRED});
     * final {@link SolrQuery} query = new {@link SolrQuery}();
     * query.addField("test", new String[] {"test1", "test2"}, mod);
     * query.toString(); // +test:(test1 test2)
     * </pre>
     * 
     * 
     * And a call to addTerm would look like this:<br>
     * <pre>
     * final QueryModifier mod = new QueryModifier({@link TermModifier#REQUIRED});
     * final {@link SolrQuery} query = new {@link SolrQuery}();
     * query.addArgument("test", mod);
     * query.toString(); // +test
     * </pre>
     * 
     * 
     * @param termModifier the term modifier
     */
    public QueryModifier(final TermModifier termModifier) {
        this.termModifier = termModifier;
        this.disjunct = false;
        this.wildcarded = false;
        this.split = false;
    }
    
    
    /**
     * This constructor sets modifier to the given modifier
     * and wildcarded, split and disjunct to false.
     * @param termModifier the term modifier
     */
    public QueryModifier(final TermModifier termModifier, final boolean disjunct) {
        this.termModifier = termModifier;
        this.disjunct = false;
        this.wildcarded = false;
        this.split = false;
    }
    

    public QueryModifier(final TermModifier termModifier, final boolean disjunct,
            final boolean wildcarded, final boolean split) {
        super();
        this.disjunct = false;
        this.termModifier = termModifier;
        this.wildcarded = wildcarded;
        this.split = split;
    }
    
    
    public String getTermPrefix() {
        return termModifier.getModifier();
    }
    
    
    public TermModifier getTermModifier() {
        return termModifier;
    }
    
    
    public boolean isDisjunct() {
        return disjunct;
    }
    
    
    public boolean isWildcarded() {
        return wildcarded;
    }
    
    
    public boolean isSplit() {
        return split;
    }
    

}
