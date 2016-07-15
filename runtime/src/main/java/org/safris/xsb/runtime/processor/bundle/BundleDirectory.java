/* Copyright (c) 2008 Seva Safris
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

package org.safris.xsb.runtime.processor.bundle;

import org.safris.commons.pipeline.PipelineDirectory;
import org.safris.commons.pipeline.PipelineEntity;
import org.safris.commons.pipeline.PipelineProcessor;
import org.safris.xsb.generator.lexer.processor.GeneratorContext;
import org.safris.xsb.generator.lexer.processor.composite.SchemaComposite;

public final class BundleDirectory implements PipelineDirectory<GeneratorContext,SchemaComposite,Bundle> {
  private BundleProcessor processor = new BundleProcessor();

  @Override
  public PipelineEntity getEntity(final SchemaComposite entity, final Bundle parent) {
    return processor;
  }

  @Override
  public PipelineProcessor<GeneratorContext,SchemaComposite,Bundle> getProcessor() {
    return processor;
  }

  @Override
  public void clear() {
  }
}