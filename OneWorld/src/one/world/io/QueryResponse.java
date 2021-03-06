/*
 * Copyright (c) 2001, University of Washington, Department of
 * Computer Science and Engineering.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither name of the University of Washington, Department of
 * Computer Science and Engineering nor the names of its contributors
 * may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package one.world.io;

import one.world.core.Event;
import one.world.core.EventHandler;
import one.world.core.InvalidTupleException;
import one.world.core.TupleException;

import one.world.binding.Duration;

/**
 * Implementation of a query response.
 *
 * @see InputRequest
 * @see one.world.util.IteratorRequest
 * @see one.world.util.IteratorResponse
 *
 * @version  $Revision: 1.8 $
 * @author   Robert Grimm
 */
public class QueryResponse extends Event {

  /**
   * The event handler for the iterator, which iterates a query's
   * results.
   *
   * @serial  Must not be <code>null</code>.
   */
  public EventHandler iter;

  /**
   * The event handler for the lease that controls the result
   * iterator.
   *
   * @serial  Must not be <code>null</code>.
   */
  public EventHandler lease;

  /**
   * The initial duration for the lease.
   *
   * @see     Duration
   *
   * @serial  Must be a valid durtion.
   */
  public long         duration;

  /** Create a new, empty query response. */
  public QueryResponse() {
    // Nothing to do.
  }

  /**
   * Create a new query response.
   *
   * @param   source    The source for the new query response.
   * @param   closure   The closure for the new query response.
   * @param   iter      The result iterator's event handler for the new
   *                    query response.
   * @param   lease     The lease's event handler for the new query
   *                    response.
   * @param   duration  The initial lease duration.
   */
  public QueryResponse(EventHandler source, Object closure,
                       EventHandler iter, EventHandler lease, long duration) {
    super(source, closure);
    this.iter     = iter;
    this.lease    = lease;
    this.duration = duration;
  }

  /** Validate this query response. */
  public void validate() throws TupleException {
    super.validate();
    if (null == iter) {
      throw new InvalidTupleException("Null iterator for query response (" +
                                      this + ")");
    } else if (null == lease) {
      throw new InvalidTupleException("Null lease for query response (" +
                                      this + ")");
    } else if (Duration.ANY >= duration) {
      throw new InvalidTupleException("Invalid duration (" + duration +
                                      ") for query response (" + this + ")");
    }
  }

}
