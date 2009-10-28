package de.cosmocode.solr;

import de.cosmocode.patterns.Immutable;

/**
 * This is an immutable class that affects the addTerm and addField methods of SolrQuery.
 * It is for that reason just a storage class, to keep the signature of the SolrQuery methods short.
 * Detailed documentation for the input types can be found in the constructors of this class.
 * 
 * @author olorenz
 *
 */
@Immutable
public final class QueryModifier {
    
    // TODO: full JavaDoc
    
    private final TermModifier termModifier;
    
    private final boolean disjunct;
    
    private final boolean wildcarded;
    
    private final boolean split;
    
    
    /**
     * This is a static shortcut to: <code>new QueryModifier(TermModifier.REQUIRED)</code>.
     */
    public static final QueryModifier REQUIRED = new QueryModifier(TermModifier.REQUIRED);
    
    /**
     * This is a static shortcut to: <code>new QueryModifier(TermModifier.PROHIBITED)</code>.
     */
    public static final QueryModifier PROHIBITED = new QueryModifier(TermModifier.PROHIBITED);
    
    /**
     * This is a static shortcut to: <code>new QueryModifier(TermModifier.PROHIBITED)</code>.
     */
    public static final QueryModifier NONE = new QueryModifier(TermModifier.NONE);
    
    
    /**
     * Default constructor.
     * Sets the modifier to NONE, and wildcarded, split and disjunct to false. 
     * 
     * 
     * A call to {@link LuceneQueryBuilder#addField(String, Object[], QueryModifier)} would look like this:<br>
     * <pre>
     * final QueryModifier mod = new QueryModifier();
     * final {@link SolrQuery} query = {@link SolrQueryFactory#getConsecutiveSolrQuery()};
     * query.addField("test", new String[] {"test1", "test2"}, mod);
     * query.toString(); // test:(test1 test2)
     * </pre>
     * 
     * 
     * And a call to addTerm would look like this:<br>
     * <pre>
     * final QueryModifier mod = new QueryModifier();
     * final {@link SolrQuery} query = {@link SolrQueryFactory#getConsecutiveSolrQuery()};
     * query.addArgument("test", mod);
     * query.toString(); // test
     * </pre>
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
     * final {@link SolrQuery} query = {@link SolrQueryFactory#getConsecutiveSolrQuery()};
     * query.addField("test", new String[] {"test1", "test2"}, mod);
     * query.toString(); // +test:(test1 test2)
     * </pre>
     * 
     * 
     * And a call to addTerm would look like this:<br>
     * <pre>
     * final QueryModifier mod = new QueryModifier({@link TermModifier#REQUIRED});
     * final {@link SolrQuery} query = {@link SolrQueryFactory#getConsecutiveSolrQuery()};
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
     * This constructor sets all values to their explicitly given values
     * @param termModifier the term modifier
     */
    public QueryModifier(final TermModifier termModifier, final boolean disjunct,
            final boolean wildcarded, final boolean split) {
        super();
        this.termModifier = termModifier;
        this.disjunct = disjunct;
        this.wildcarded = wildcarded;
        this.split = split;
    }
    
    
    /**
     * This method returns the term prefix.
     * The term prefix is written in front of the term or field
     * and affects the number and content of documents returned.
     * 
     * @see TermModifier
     * @return
     */
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
    
    
    /**
     * Returns a new QueryModifier which is a copy of `mod`
     * with the exception of the termModifier, which is set to `tm`.
     * @param mod the original QueryModifier to merge
     * @param tm the new TermModifier for the new QueryModifier
     * @return a new QueryModifier which is a merged version of `mod` and `tm`
     */
    public static QueryModifier merge(final QueryModifier mod, final TermModifier tm) {
        return new QueryModifier(tm, mod.isDisjunct(), mod.isWildcarded(), mod.isSplit());
    }
    

}
