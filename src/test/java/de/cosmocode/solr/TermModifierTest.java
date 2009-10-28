package de.cosmocode.solr;

import static org.junit.Assert.*;

import org.junit.Test;

public class TermModifierTest {

    @Test
    public void testGetModifier() {
        assertEquals("", TermModifier.NONE.getModifier());
        assertEquals("+", TermModifier.REQUIRED.getModifier());
        assertEquals("-", TermModifier.PROHIBITED.getModifier());
    }

}
