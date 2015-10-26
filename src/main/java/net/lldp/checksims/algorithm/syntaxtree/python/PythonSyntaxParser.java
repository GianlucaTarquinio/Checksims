/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2015 Ted Meyer and Michael Andrews
 */
package net.lldp.checksims.algorithm.syntaxtree.python;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.lldp.checksims.parse.Percentable;
import net.lldp.checksims.parse.ast.AST;
import net.lldp.checksims.parse.ast.ASTFactory;
import net.lldp.checksims.parse.ast.LanguageDependantSyntaxParser;
import net.lldp.checksims.parse.ast.python.Python3Lexer;
import net.lldp.checksims.parse.ast.python.Python3Parser;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.util.data.Real;

/**
 * 
 * @author ted
 * A language dependant syntax parser for generating ASTs from java source
 */
public class PythonSyntaxParser implements LanguageDependantSyntaxParser
{

    @Override
    public ParseTreeVisitor<AST> getTreeWalker()
    {
        return new SuperQuickTreeWalker();
    }

    @Override
    public Set<ParserRuleContext> sourceToDefaultcontext(Submission s, String contentAsString)
    {
        
        Python3Parser j8p = ASTFactory.makeParser(s.getName(), new ANTLRInputStream(contentAsString), Python3Parser.class, Python3Lexer.class);
        
        Set<ParserRuleContext> result = new HashSet<>();
        boolean endOfFile = true;
        while(endOfFile)
        {
            try
            {
                result.add(j8p.single_input());
            }
            catch(ASTFactory.EOFParsingException epe)
            {
                endOfFile = false;
            }
            catch(ASTFactory.SyntaxErrorException see)
            {
                System.out.println("Syntax Error for assignment: " + s.getName());
                return new HashSet<>();
            }
        }
        
        return result;
    }

}

/**
 * 
 * @author ted
 * sometimes students submit bad code, what else can I say?
 */
class InvalidSubmission implements Percentable
{
    @Override
    public Real getPercentageMatched()
    {
        return new Real(-1);
    }
}
