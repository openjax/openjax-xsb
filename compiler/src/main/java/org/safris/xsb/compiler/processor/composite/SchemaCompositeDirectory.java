/* Copyright (c) 2008 lib4j
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

package org.safris.xsb.compiler.processor.composite;

import org.lib4j.pipeline.PipelineDirectory;
import org.lib4j.pipeline.PipelineEntity;
import org.lib4j.pipeline.PipelineProcessor;
import org.safris.xsb.compiler.processor.GeneratorContext;
import org.safris.xsb.compiler.processor.document.SchemaDocument;

public final class SchemaCompositeDirectory implements PipelineDirectory<GeneratorContext,SchemaDocument,SchemaComposite> {
  private SchemaCompositeProcessor processor = new SchemaCompositeProcessor();

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