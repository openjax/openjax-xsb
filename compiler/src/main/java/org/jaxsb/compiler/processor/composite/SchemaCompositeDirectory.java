/* Copyright (c) 2008 JAX-SB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.jaxsb.compiler.processor.composite;

import org.jaxsb.compiler.pipeline.PipelineDirectory;
import org.jaxsb.compiler.pipeline.PipelineEntity;
import org.jaxsb.compiler.pipeline.PipelineProcessor;
import org.jaxsb.compiler.processor.GeneratorContext;
import org.jaxsb.compiler.processor.document.SchemaDocument;

public final class SchemaCompositeDirectory implements PipelineDirectory<GeneratorContext,SchemaDocument,SchemaComposite> {
  private final SchemaCompositeProcessor processor = new SchemaCompositeProcessor();

  @Override
  public PipelineEntity getEntity(final SchemaDocument entity, final SchemaComposite parent) {
    return processor;
  }

  @Override
  public PipelineProcessor<GeneratorContext,SchemaDocument,SchemaComposite> getProcessor() {
    return processor;
  }

  @Override
  public void clear() {
  }
}