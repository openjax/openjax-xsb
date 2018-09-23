/* Copyright (c) 2008 OpenJAX
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

package org.openjax.xsb.generator.processor.plan;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openjax.xsb.compiler.processor.GeneratorContext;
import org.openjax.xsb.compiler.processor.model.Model;
import org.openjax.xsb.compiler.processor.model.element.AllModel;
import org.openjax.xsb.compiler.processor.model.element.AnnotationModel;
import org.openjax.xsb.compiler.processor.model.element.AnyAttributeModel;
import org.openjax.xsb.compiler.processor.model.element.AnyModel;
import org.openjax.xsb.compiler.processor.model.element.AppinfoModel;
import org.openjax.xsb.compiler.processor.model.element.AttributeGroupModel;
import org.openjax.xsb.compiler.processor.model.element.AttributeModel;
import org.openjax.xsb.compiler.processor.model.element.ChoiceModel;
import org.openjax.xsb.compiler.processor.model.element.ComplexContentModel;
import org.openjax.xsb.compiler.processor.model.element.ComplexTypeModel;
import org.openjax.xsb.compiler.processor.model.element.DocumentationModel;
import org.openjax.xsb.compiler.processor.model.element.ElementModel;
import org.openjax.xsb.compiler.processor.model.element.EnumerationModel;
import org.openjax.xsb.compiler.processor.model.element.ExtensionModel;
import org.openjax.xsb.compiler.processor.model.element.FieldModel;
import org.openjax.xsb.compiler.processor.model.element.FractionDigitsModel;
import org.openjax.xsb.compiler.processor.model.element.GroupModel;
import org.openjax.xsb.compiler.processor.model.element.HasFacetModel;
import org.openjax.xsb.compiler.processor.model.element.HasPropertyModel;
import org.openjax.xsb.compiler.processor.model.element.ImportModel;
import org.openjax.xsb.compiler.processor.model.element.IncludeModel;
import org.openjax.xsb.compiler.processor.model.element.KeyModel;
import org.openjax.xsb.compiler.processor.model.element.KeyrefModel;
import org.openjax.xsb.compiler.processor.model.element.LengthModel;
import org.openjax.xsb.compiler.processor.model.element.ListModel;
import org.openjax.xsb.compiler.processor.model.element.MaxExclusiveModel;
import org.openjax.xsb.compiler.processor.model.element.MaxInclusiveModel;
import org.openjax.xsb.compiler.processor.model.element.MaxLengthModel;
import org.openjax.xsb.compiler.processor.model.element.MinExclusiveModel;
import org.openjax.xsb.compiler.processor.model.element.MinInclusiveModel;
import org.openjax.xsb.compiler.processor.model.element.MinLengthModel;
import org.openjax.xsb.compiler.processor.model.element.NotationModel;
import org.openjax.xsb.compiler.processor.model.element.PatternModel;
import org.openjax.xsb.compiler.processor.model.element.RedefineModel;
import org.openjax.xsb.compiler.processor.model.element.RestrictionModel;
import org.openjax.xsb.compiler.processor.model.element.SchemaModel;
import org.openjax.xsb.compiler.processor.model.element.SelectorModel;
import org.openjax.xsb.compiler.processor.model.element.SequenceModel;
import org.openjax.xsb.compiler.processor.model.element.SimpleContentModel;
import org.openjax.xsb.compiler.processor.model.element.SimpleTypeModel;
import org.openjax.xsb.compiler.processor.model.element.UnionModel;
import org.openjax.xsb.compiler.processor.model.element.UniqueModel;
import org.openjax.xsb.compiler.processor.model.element.WhiteSpaceModel;
import org.openjax.xsb.generator.processor.plan.element.AllPlan;
import org.openjax.xsb.generator.processor.plan.element.AnnotationPlan;
import org.openjax.xsb.generator.processor.plan.element.AnyAttributePlan;
import org.openjax.xsb.generator.processor.plan.element.AnyPlan;
import org.openjax.xsb.generator.processor.plan.element.AppinfoPlan;
import org.openjax.xsb.generator.processor.plan.element.AttributeGroupPlan;
import org.openjax.xsb.generator.processor.plan.element.AttributePlan;
import org.openjax.xsb.generator.processor.plan.element.ChoicePlan;
import org.openjax.xsb.generator.processor.plan.element.ComplexContentPlan;
import org.openjax.xsb.generator.processor.plan.element.ComplexTypePlan;
import org.openjax.xsb.generator.processor.plan.element.DocumentationPlan;
import org.openjax.xsb.generator.processor.plan.element.ElementPlan;
import org.openjax.xsb.generator.processor.plan.element.EnumerationPlan;
import org.openjax.xsb.generator.processor.plan.element.ExtensionPlan;
import org.openjax.xsb.generator.processor.plan.element.FieldPlan;
import org.openjax.xsb.generator.processor.plan.element.FractionDigitsPlan;
import org.openjax.xsb.generator.processor.plan.element.GroupPlan;
import org.openjax.xsb.generator.processor.plan.element.HasFacetPlan;
import org.openjax.xsb.generator.processor.plan.element.HasPropertyPlan;
import org.openjax.xsb.generator.processor.plan.element.ImportPlan;
import org.openjax.xsb.generator.processor.plan.element.IncludePlan;
import org.openjax.xsb.generator.processor.plan.element.KeyPlan;
import org.openjax.xsb.generator.processor.plan.element.KeyrefPlan;
import org.openjax.xsb.generator.processor.plan.element.LengthPlan;
import org.openjax.xsb.generator.processor.plan.element.ListPlan;
import org.openjax.xsb.generator.processor.plan.element.MaxExclusivePlan;
import org.openjax.xsb.generator.processor.plan.element.MaxInclusivePlan;
import org.openjax.xsb.generator.processor.plan.element.MaxLengthPlan;
import org.openjax.xsb.generator.processor.plan.element.MinExclusivePlan;
import org.openjax.xsb.generator.processor.plan.element.MinInclusivePlan;
import org.openjax.xsb.generator.processor.plan.element.MinLengthPlan;
import org.openjax.xsb.generator.processor.plan.element.NotationPlan;
import org.openjax.xsb.generator.processor.plan.element.PatternPlan;
import org.openjax.xsb.generator.processor.plan.element.RedefinePlan;
import org.openjax.xsb.generator.processor.plan.element.RestrictionPlan;
import org.openjax.xsb.generator.processor.plan.element.SchemaPlan;
import org.openjax.xsb.generator.processor.plan.element.SelectorPlan;
import org.openjax.xsb.generator.processor.plan.element.SequencePlan;
import org.openjax.xsb.generator.processor.plan.element.SimpleContentPlan;
import org.openjax.xsb.generator.processor.plan.element.SimpleTypePlan;
import org.openjax.xsb.generator.processor.plan.element.UnionPlan;
import org.openjax.xsb.generator.processor.plan.element.UniquePlan;
import org.openjax.xsb.generator.processor.plan.element.WhiteSpacePlan;
import org.openjax.xsb.helper.pipeline.PipelineDirectory;
import org.openjax.xsb.helper.pipeline.PipelineEntity;
import org.openjax.xsb.helper.pipeline.PipelineProcessor;
import org.openjax.xsb.runtime.CompilerFailureException;

public final class PlanDirectory implements PipelineDirectory<GeneratorContext,Model,Plan<?>> {
  private final Map<Class<?>,Class<?>> classes = new HashMap<>(39);
  private final Collection<Class<?>> keys;
  private final PlanProcessor processor = new PlanProcessor();

  public PlanDirectory() {
    classes.put(AllModel.class, AllPlan.class);
    classes.put(AnnotationModel.class, AnnotationPlan.class);
    classes.put(AnyAttributeModel.class, AnyAttributePlan.class);
    classes.put(AnyModel.class, AnyPlan.class);
    classes.put(AppinfoModel.class, AppinfoPlan.class);
    classes.put(AttributeGroupModel.class, AttributeGroupPlan.class);
    classes.put(AttributeModel.class, AttributePlan.class);
    classes.put(ChoiceModel.class, ChoicePlan.class);
    classes.put(ComplexContentModel.class, ComplexContentPlan.class);
    classes.put(ComplexTypeModel.class, ComplexTypePlan.class);
    classes.put(DocumentationModel.class, DocumentationPlan.class);
    classes.put(ElementModel.class, ElementPlan.class);
    classes.put(EnumerationModel.class, EnumerationPlan.class);
    classes.put(ExtensionModel.class, ExtensionPlan.class);
    classes.put(FieldModel.class, FieldPlan.class);
    classes.put(FractionDigitsModel.class, FractionDigitsPlan.class);
    classes.put(GroupModel.class, GroupPlan.class);
    classes.put(HasFacetModel.class, HasFacetPlan.class);
    classes.put(HasPropertyModel.class, HasPropertyPlan.class);
    classes.put(ImportModel.class, ImportPlan.class);
    classes.put(IncludeModel.class, IncludePlan.class);
    classes.put(KeyModel.class, KeyPlan.class);
    classes.put(KeyrefModel.class, KeyrefPlan.class);
    classes.put(LengthModel.class, LengthPlan.class);
    classes.put(ListModel.class, ListPlan.class);
    classes.put(MaxExclusiveModel.class, MaxExclusivePlan.class);
    classes.put(MaxInclusiveModel.class, MaxInclusivePlan.class);
    classes.put(MaxLengthModel.class, MaxLengthPlan.class);
    classes.put(MinExclusiveModel.class, MinExclusivePlan.class);
    classes.put(MinInclusiveModel.class, MinInclusivePlan.class);
    classes.put(MinLengthModel.class, MinLengthPlan.class);
    classes.put(NotationModel.class, NotationPlan.class);
    classes.put(PatternModel.class, PatternPlan.class);
    classes.put(RedefineModel.class, RedefinePlan.class);
    classes.put(RestrictionModel.class, RestrictionPlan.class);
    classes.put(SchemaModel.class, SchemaPlan.class);
    classes.put(SelectorModel.class, SelectorPlan.class);
    classes.put(SequenceModel.class, SequencePlan.class);
    classes.put(SimpleContentModel.class, SimpleContentPlan.class);
    classes.put(SimpleTypeModel.class, SimpleTypePlan.class);
    classes.put(UnionModel.class, UnionPlan.class);
    classes.put(UniqueModel.class, UniquePlan.class);
    classes.put(WhiteSpaceModel.class, WhiteSpacePlan.class);
    keys = classes.keySet();
  }

  @Override
  public PipelineEntity getEntity(final Model entity, final Plan<?> parent) {
    if (!keys.contains(entity.getClass()))
      throw new IllegalArgumentException("Unknown key: " + entity.getClass().getSimpleName());

    final Class<?> parserClass = classes.get(entity.getClass());
    Plan<?> plan = null;
    try {
      final Constructor<?> constructor = parserClass.getConstructor(entity.getClass(), Plan.class);
      plan = (Plan<?>)constructor.newInstance(entity, parent);
      return plan;
    }
    catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new CompilerFailureException(e);
    }
  }

  @Override
  public PipelineProcessor<GeneratorContext,Model,Plan<?>> getProcessor() {
    return processor;
  }

  @Override
  public void clear() {
  }
}