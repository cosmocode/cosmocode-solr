package de.cosmocode.lucene;

import de.cosmocode.patterns.Immutable;
import de.cosmocode.solr.SolrQuery;
import de.cosmocode.solr.SolrQueryFactory;

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
	
	public static final String ERR_TERMMOD_NULL = 
		"the given TermModifier must not be null";
	
	public static final String ERR_FUZZYNESS_INVALID = 
		"the given fuzzyness must be between 0 (inclusive) and 1 (exclusive)";
	
	public static final String ERR_FUZZYNESS_DISABLED = 
		"fuzzyness is not enabled";
	
	/**
	 * The default QueryModifier.
	 * It has termModifier set to NONE,
	 * split, disjunct and wildcarded are false
	 * and fuzzyness is disabled.
	 */
	public static final QueryModifier DEFAULT = start().end();
	
	
	private final TermModifier termModifier;
	private final boolean split;
	private final boolean disjunct;
	private final boolean wildcarded;
	private final Double fuzzyness;

	
	public QueryModifier(TermModifier termModifier, boolean split,
			boolean disjunct, boolean wildcarded, Double fuzzyness) {
		super();
		
		if (termModifier == null) throw new NullPointerException(ERR_TERMMOD_NULL);
		if (fuzzyness != null) {
			if (fuzzyness < 0 || fuzzyness >= 1) throw new IllegalArgumentException(ERR_FUZZYNESS_INVALID);
		}
		
		this.termModifier = termModifier;
		this.split = split;
		this.disjunct = disjunct;
		this.wildcarded = wildcarded;
		this.fuzzyness = fuzzyness;
	}
	
	
    /**
     * This method returns the term prefix.
     * The term prefix is written in front of the term or field
     * and affects the number and content of documents returned.
     * 
     * @see TermModifier
     * @return the term prefix for the Term
     */
    public String getTermPrefix() {
        return termModifier.getModifier();
    }
	
	public TermModifier getTermModifier() {
		return termModifier;
	}
	
	/**
	 * Returns the QueryModifier for the values of a field.
	 * @return the QueryModifier for the values of a field
	 */
	public QueryModifier getFieldValueModifier() {
		if (disjunct) {
			return new QueryModifier(TermModifier.NONE, split, disjunct, wildcarded, fuzzyness);
		} else {
			return new QueryModifier(TermModifier.REQUIRED, split, disjunct, wildcarded, fuzzyness);
		}
	}
	
	public boolean isDisjunct() {
		return disjunct;
	}
	
	public boolean isSplit() {
		return split;
	}
	
	public boolean isWildcarded() {
		return wildcarded;
	}
	
	public boolean isFuzzyEnabled() {
		return fuzzyness != null;
	}
	
	/**
	 * Returns the fuzzyness for a fuzzy search.
	 * Throws an IllegalStateException if fuzzyness is not enabled.
	 * @return the fuzzyness of the search
	 * @throws IllegalStateException if fuzzyness is disabled. Check with {@link #isFuzzyEnabled()}
	 * 
	 * @see #isFuzzyEnabled()
	 */
	public double getFuzzyness() {
		if (fuzzyness == null) throw new IllegalStateException(ERR_FUZZYNESS_DISABLED);
		return fuzzyness.doubleValue();
	}
	
	
	/* 
	 * Builder Pattern
	 * The Builder is a static class, and QueryModifier has some helper methods
	 */
	
	public QueryModifier.Builder copy() {
		return copyOf(this);
	}
	
	public static QueryModifier.Builder copyOf(final QueryModifier mod) {
		final QueryModifier.Builder builder = new QueryModifier.Builder();
		builder.setTermModifier(mod.termModifier);
		builder.setSplit(mod.isSplit());
		builder.setDisjunct(mod.isDisjunct());
		builder.setWildcarded(mod.isWildcarded());
		builder.setFuzzyness(mod.fuzzyness);
		return builder;
	}
	
	public static QueryModifier.Builder start() {
		final QueryModifier.Builder builder = new QueryModifier.Builder();
		builder.setTermModifier(TermModifier.NONE);
		return builder;
	}
	
	public static class Builder {
		
		private TermModifier termModifier;
		private boolean split;
		private boolean disjunct;
		private boolean wildcarded;
		private Double fuzzyness;
		
		
		public Builder() {
			this.termModifier = TermModifier.NONE;
		}
		
		
		public Builder setTermModifier(TermModifier termModifier) {
			if (termModifier == null) throw new NullPointerException(ERR_TERMMOD_NULL);
			this.termModifier = termModifier;
			return this;
		}
		
		public Builder setSplit(boolean split) {
			this.split = split;
			return this;
		}
		
		public Builder setDisjunct(boolean disjunct) {
			this.disjunct = disjunct;
			return this;
		}
		
		public Builder setWildcarded(boolean wildcarded) {
			this.wildcarded = wildcarded;
			return this;
		}
		
		public Builder setFuzzyness(Double fuzzyness) {
			if (fuzzyness != null) {
				if (fuzzyness < 0 || fuzzyness >= 1) throw new IllegalArgumentException(ERR_FUZZYNESS_INVALID);
			}
			this.fuzzyness = fuzzyness;
			return this;
		}
		
		
		public Builder wildcarded() {
			this.wildcarded = true;
			return this;
		}
		
		public Builder notWildcarded() {
			this.wildcarded = false;
			return this;
		}
		
		public Builder doSplit() {
			this.split = true;
			return this;
		}
		
		public Builder dontSplit() {
			this.split = false;
			return this;
		}
		
		public Builder disjunct() {
			this.disjunct = true;
			return this;
		}
		
		public Builder conjunct() {
			this.disjunct = false;
			return this;
		}
		
		public Builder noFuzzyness() {
			this.fuzzyness = null;
			return this;
		}
		
		
		public QueryModifier end() {
			return new QueryModifier(termModifier, split, disjunct, wildcarded, fuzzyness);
		}
	}
	
}