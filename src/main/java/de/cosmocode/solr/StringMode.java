/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * All rights reserved
 */

package de.cosmocode.solr;

/**
 * Used for the {@link UtilityDocumentBuilder} in
 * {@link UtilityDocumentBuilder#addDynamicField(String, String, StringMode)}.
 *  
 * @author Oliver Lorenz
 */
public enum StringMode {
    
    /**
     * <p> The given String is treated as a plain string.
     * The input is not split by lucene/solr, so this is a good option
     * for sorting, but not for searching.
     * This is a good option for unique data like ids.
     * </p>
     */
    STRING,
    
    /**
     * <p> The given String is treated as a complex text.
     * The input is split into its words and stemmed by lucene/solr,
     * so this is a good option for searching, but not for sorting.
     * This is a good option for some general purpose text, like a description.
     * </p>
     */
    TEXT,
    
    /**
     * <p> The given String is treated as multiple words, separated by blanks.
     * The input is split into its words by lucene/solr.
     * This is a good option for for a set of unique values, like enums.
     */
    SPLIT;

}
