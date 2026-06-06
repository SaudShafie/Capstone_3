package org.example.capstone_3.AI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiJsonParserTest {

    @Test
    void parseObject_readsBooleanField() {
        var node = AiJsonParser.parseObject("{\"isCorrect\": true}");
        assertTrue(AiJsonParser.requireBoolean(node, "isCorrect"));
    }

    @Test
    void parseObject_readsScoreAndFeedback() {
        var node = AiJsonParser.parseObject("{\"score\": 85, \"feedback\": \"Good work.\"}");
        assertEquals(85, AiJsonParser.requireInt(node, "score", 0, 100));
        assertEquals("Good work.", AiJsonParser.requireText(node, "feedback"));
    }

    @Test
    void requireInt_rejectsOutOfRange() {
        var node = AiJsonParser.parseObject("{\"score\": 150}");
        assertThrows(AiException.class, () -> AiJsonParser.requireInt(node, "score", 0, 100));
    }
}
