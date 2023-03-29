// SAD error handler.
// http://www.saxproject.org
// No warranty; no copyright -- use this as you will.
// $Id: ErrorHandler.java 2071 2023-03-28 14:51:20Z carl $

package com.optimasc.streams;

/**
 * Basic interface for SAD error handlers.
 *
 * <p>If a SAD application needs to implement customized error
 * handling, it must implement this interface and then register an
 * instance with the SAD reader using the
 * {@link com.optimasc.sad.SADReader#setErrorHandler setErrorHandler}
 * method.  The parser will then report all errors and warnings
 * through this interface.</p>
 *
 * <p><strong>WARNING:</strong> If an application does <em>not</em>
 * register an ErrorHandler, SAD parsing errors will go unreported,
 * except that <em>SADParseException</em>s will be thrown for fatal errors.
 * In order to detect validity errors, an ErrorHandler that does something
 * with {@link #error error()} calls must be registered.</p>
 *
 * <p>For documents processing errors, a SAX driver must use this interface 
 * in preference to throwing an exception: it is up to the application 
 * to decide whether to throw an exception for different types of 
 * errors and warnings.  Note, however, that there is no requirement that 
 * the parser continue to report additional errors after a call to 
 * {@link #fatalError fatalError}.  In other words, a SAD driver class 
 * may throw an exception after reporting any fatalError.
 * Also parsers may throw appropriate exceptions for non document specific errors.
 * For example, {@link SADReader#parse SADReader.parse()} would throw
 * an IOException for errors accessing entities or the document.</p>
 *
 */
public interface ErrorHandler {
    
    
    /**
     * Receive notification of a warning.
     *
     * <p>SAD parsers will use this method to report conditions that
     * are not errors or fatal errors as defined by the official document
     * specification.  The default behaviour is to take no
     * action.</p>
     *
     * <p>The SAD parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.</p>
     *
     * <p>Filters may use this method to report other, non document warnings
     * as well.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  SAD parse exception.
     * @exception com.optimasc.sad.DocumentStreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see com.optimasc.sad.DocumentStreamException 
     */
    public abstract void warning (DocumentStreamException exception)
	throws DocumentStreamException;
    
    
    /**
     * Receive notification of a recoverable error.
     *
     * <p>This corresponds to the definition of "error" 
     * of the Official Document Specification.  For example, a validating
     * parser would use this callback to report the violation of a
     * validity constraint.  The default behaviour is to take no
     * action.</p>
     *
     * <p>The SAD parser must continue to provide normal parsing
     * events after invoking this method: it should still be possible
     * for the application to process the document through to the end.
     * If the application cannot do so, then the parser should report
     * a fatal error even if the official specification does not require
     * it to do so.</p>
     *
     * <p>Filters may use this method to report other, non document errors
     * as well.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAD parse exception.
     * @exception com.optimasc.sad.DocumentStreamException Any SAD exception, possibly
     *            wrapping another exception.
     * @see com.optimasc.sad.DocumentStreamException 
     */
    public abstract void error (DocumentStreamException exception) throws DocumentStreamException;
    
    
    /**
     * Receive notification of a non-recoverable error.
     *
     * A parser would use this callback to report the violation of a
     * well-formedness constraint.</p>
     *
     * <p>The application must assume that the document is unusable
     * after the parser has invoked this method, and should continue
     * (if at all) only for the sake of collecting additional error
     * messages: in fact, SAD parsers are free to stop reporting any
     * other events once this method has been invoked.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAD parse exception.  
     * @exception com.optimasc.sad.DocumentStreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see com.optimasc.sad.DocumentStreamException
     */
    public abstract void fatalError (DocumentStreamException exception) throws DocumentStreamException;
    
}

