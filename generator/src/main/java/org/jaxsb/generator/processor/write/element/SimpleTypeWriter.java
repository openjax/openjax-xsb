/* Copyright (c) 2008 JAX-SB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without enm, including without limitation the rights
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

package org.jaxsb.generator.processor.write.element;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.jaxsb.generator.processor.plan.EnumerablePlan;
import org.jaxsb.generator.processor.plan.ExtensiblePlan;
import org.jaxsb.generator.processor.plan.Plan;
import org.jaxsb.generator.processor.plan.element.EnumerationPlan;
import org.jaxsb.generator.processor.plan.element.PatternPlan;
import org.jaxsb.generator.processor.plan.element.SimpleTypePlan;
import org.jaxsb.generator.processor.write.Writer;
import org.jaxsb.runtime.CompilerFailureException;
import org.jaxsb.runtime.Enum;
import org.jaxsb.runtime.MarshalException;
import org.jaxsb.runtime.NotationType;
import org.jaxsb.runtime.SimpleType;
import org.jaxsb.runtime.XSTypeDirectory;
import org.libj.util.CollectionUtil;
import org.w3.www._2001.XMLSchema.yAA.$ID;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class SimpleTypeWriter<T extends SimpleTypePlan<?>> extends Writer<T> {
  protected static void writeQualifiedName(final StringWriter writer, final SimpleTypePlan<?> plan) {
    if (plan.getId() != null)
      writer.write("@" + org.jaxsb.runtime.Id.class.getName() + "(\"" + plan.getId() + "\")\n");

    writer.write("@" + org.jaxsb.runtime.QName.class.getName() + "(namespaceURI=\"" + plan.getName().getNamespaceURI() +"\", localPart=\"" + plan.getName().getLocalPart() +"\", prefix=\"" + plan.getName().getPrefix() +"\")\n");
  }

  protected static void writeIsNull(final StringWriter writer) {
    writer.write("public boolean isNull()\n");
    writer.write("{\n");
    writer.write("return super.isNull();\n");
    writer.write("}\n");
  }

  protected static void writeIdLookup(final StringWriter writer, final SimpleTypePlan<?> plan, final Plan<?> parent) {
    if (!$ID.class.getCanonicalName().equals(plan.getSuperClassNameWithoutType()))
      return;

    final String className;
    final String instanceName;
    if (plan.hasEnumerations()) {
      if (((EnumerablePlan)plan).hasSuperEnumerations())
        className = ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + ".Enum";
      else
        className = "Enum";

      instanceName = "id.text()";
    }
    else {
      className = String.class.getName();
      instanceName = "id";
    }

    writer.write("public static " + plan.getClassName(parent) + " lookupId(final " + className + " id)\n");
    writer.write("{\n");
    writer.write("final " + Map.class.getName() + "<" + String.class.getName() + ",? extends " + $ID.class.getCanonicalName() + "> idMap = namespaceIds.get(NAME.getNamespaceURI());\n");
    writer.write("if (idMap == null)\n");
    writer.write("return null;\n");
    writer.write("final " + $ID.class.getCanonicalName() + " value = idMap.get(" + instanceName + ");\n");
    writer.write("if (value instanceof " + plan.getClassName(parent) + ")\n");
    writer.write("return (" + plan.getClassName(parent) + ")value;\n");
    writer.write("return null;\n");
    writer.write("}\n");

    writer.write("public static " + Collection.class.getName() + "<" + plan.getClassName(parent) + "> lookupId()\n");
    writer.write("{\n");
    writer.write("final " + Map.class.getName() + "<" + String.class.getName() + ",? extends " + $ID.class.getCanonicalName() + "> idMap = namespaceIds.get(NAME.getNamespaceURI());\n");
    writer.write("if (idMap == null)\n");
    writer.write("return null;\n");
    writer.write("final " + Collection.class.getName() + "<" + plan.getClassName(parent) + "> ids = new " + ArrayList.class.getName() + "<>();\n");
    writer.write("for(" + $ID.class.getCanonicalName() + " id : idMap.values())\n");
    writer.write("if (id.getClass().equals(" + plan.getClassName(parent) + ".class))\n");
    writer.write("ids.add((" + plan.getClassName(parent) + ")id);\n");
    writer.write("return ids;\n");
    writer.write("}\n");
  }

  protected static void getNativeConstructors(final StringWriter writer, final SimpleTypePlan<?> plan) {
    if (plan.getNativeItemClassNameInterface() == null || (plan.isList() && plan.hasEnumerations()))
      return;

    writer.write(plan.getDocumentation());
    final String visibility;
    if (((EnumerablePlan)plan).hasEnumerations() && !plan.isUnionWithNonEnumeration()) {
      visibility = "protected ";
    }
    else {
      // DOCUMENTATION
      visibility = "public ";
    }

    if (plan.isList()) {
      final String genericType = "<T extends " + plan.getNativeNonEnumItemClassNameInterface() + " & " + Serializable.class.getName() + ">";
      writer.write(visibility + genericType + plan.getClassSimpleName() + "(final T text)\n");
      writer.write("{\n");
      writer.write("super(text);\n");
      writer.write("}\n");

      writer.write(visibility + genericType + plan.getClassSimpleName() + "(final " + plan.getNativeNonEnumItemClassName() + " ... text)\n");
      writer.write("{\n");
      writer.write("super((T)" + Arrays.class.getName() + ".asList(text));\n");
      writer.write("}\n");
    }
    else {
      writer.write(visibility + plan.getClassSimpleName() + "(final " + plan.getNativeNonEnumItemClassNameInterface() + " text)\n");
      writer.write("{\n");
      writer.write("super(text);\n");
      writer.write("}\n");
    }

//      if (plan.getNativeItemClassName() == null && XSTypeDirectory.ANYSIMPLETYPE.getNativeBinding().getName().equals(plan.getBaseXSItemTypeName()))
//      {
//          writer.write(accessibility + plan.getClassSimpleName() + "(" + List.class.getName() + "<" + plan.getNativeItemClassNameInterface() + "> text)\n");
//          writer.write("{\n");
//          writer.write("super(text);\n");
//          writer.write("}\n");
//      }
  }

  protected static void getRestrictions(final StringWriter writer, final SimpleTypePlan<?> plan) {
    if (!plan.hasEnumerations())
      return;

    if (!((EnumerablePlan)plan).hasEnumerations())
      return;

    final boolean hasEnumerations = ((EnumerablePlan)plan).hasEnumerations();
    final boolean hasSuperEnumerations = ((EnumerablePlan)plan).hasSuperEnumerations();

    final boolean isNotation = NotationType.class.getName().equals(plan.getNativeItemClassName());
    if (hasEnumerations) {
      if (isNotation) {
        writer.write("static {\n");
        for (final EnumerationPlan enumeration : ((EnumerablePlan)plan).getEnumerations())
          writer.write("lookupElement(new " + QName.class.getName() + "(\"" + enumeration.getValue().getNamespaceURI() + "\", \"" + enumeration.getValue().getLocalPart() + "\"), " + Thread.class.getName() + ".currentThread().getContextClassLoader());\n");

        writer.write("}\n");
      }

      for (final EnumerationPlan enumeration : ((EnumerablePlan)plan).getEnumerations()) {
        final String value;
        if (isNotation)
          value = "new " + QName.class.getName() + "(\"" + enumeration.getValue().getNamespaceURI() + "\", \"" + enumeration.getValue().getLocalPart() + "\")";
        else if (XSTypeDirectory.QNAME.getNativeBinding().getName().equals(plan.getBaseXSItemTypeName()))
          value = "\"" + enumeration.getValue() + "\"";
        else
          value = "\"" + enumeration.getValue().getLocalPart() + "\"";

        writer.write("public static final Enum " + enumeration.getDeclarationName() + " = new Enum(" + value + ");\n");
      }
    }

    final String enumType = isNotation ? QName.class.getName() : String.class.getName();

    writer.write("public static class Enum");
    if (hasSuperEnumerations) {
      writer.write(" extends " + ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + ".Enum\n{\n");
      writer.write("protected static " + Map.class.getName() + "<" + enumType + "," + ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + ".Enum> values()\n{\nreturn " + ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + ".Enum.values();\n};\n");
    }
    else {
      writer.write(" implements " + Enum.class.getName() + "<" + plan.getNativeItemClassName() + ">\n{\n");
      writer.write("protected static final " + Map.class.getName() + "<" + enumType + ",Enum> values = new " + HashMap.class.getName() + "<>();\n");
      writer.write("protected static " + Map.class.getName() + "<" + enumType + ",Enum> values()\n{\nreturn values;\n};\n");
      writer.write("public static Enum valueOf(final " + enumType + " s)\n{\nreturn values.get(s);\n};\n");
    }

    if (hasEnumerations) {
      if (hasSuperEnumerations) {
        writer.write("@" + Override.class.getName() + "\n");
        writer.write("public int ordinal()\n{\n");
        writer.write("return super.ordinal();\n}\n");
      }
      else {
        writer.write("protected final " + plan.getNativeItemClassName() + " text;\n");
        writer.write("protected final int ordinal;\n");
        writer.write("@" + Override.class.getName() + "\n");
        writer.write("public int ordinal()\n{\n");
        writer.write("return ordinal;\n}\n");
      }
    }

    writer.write("protected Enum(final " + enumType + " text)\n");
    writer.write("{\n");

    if (hasSuperEnumerations) {
      writer.write("super(text);\n");
    }
    else if (hasEnumerations) {
      if (plan.getNativeFactory() != null)
        writer.write("this.text = " + plan.getNativeFactory() + "(text);\n");
      else
        writer.write("this.text = text;\n");

      writer.write("this.ordinal = values.size();\n");
      writer.write("values.put(text, this);\n");
    }

    writer.write("}\n");

    if (hasEnumerations && !hasSuperEnumerations) {
      writer.write("@" + Override.class.getName() + "\n");
      writer.write("public " + plan.getNativeItemClassName() + " text()\n");
      writer.write("{\n");
      writer.write("return text;\n");
      writer.write("}\n");
    }

    writer.write("}\n");

    final String enmClassName;
    if (hasSuperEnumerations)
      enmClassName = ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + ".Enum";
    else
      enmClassName = "Enum";

    // DOCUMENTATION
    writer.write(plan.getDocumentation());

    if (plan.isList()) {
      writer.write("public int[] ordinal()\n");
      writer.write("{\n");
      writer.write("if (text() == null)\n");
      writer.write("return null;\n");
      writer.write("final int[] ordinals = new int[text().size()];\n");
      writer.write("for (int i = 0; i < ordinals.length; ++i) {\n");
      writer.write("final " + (hasSuperEnumerations ? ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + "." : "") + "Enum enm = Enum.values().get(text().get(i));\n");
      writer.write("ordinals[i] = Enum.values().get(text().get(i)).ordinal();\n");
      writer.write("}\n");
      writer.write("return ordinals;\n");
      writer.write("}\n");

      writer.write("public " + plan.getClassSimpleName() + "(final " + List.class.getName() + "<" + enmClassName + "> enms)\n");
      writer.write("{\n");
      writer.write("super.text(new " + plan.getNativeItemClassNameImplementation() + "());\n");
      writer.write("for(" + enmClassName + " temp : enms)\n");
      writer.write("if (temp != null)\n");
      writer.write("((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text()).add(temp.text);\n");
      writer.write("}\n");

      writer.write("public " + plan.getClassSimpleName() + "(final " + enmClassName + " ... enms)\n");
      writer.write("{\n");
      writer.write("super.text(new " + plan.getNativeItemClassNameImplementation() + "());\n");
      writer.write("for(" + enmClassName + " temp : enms)\n");
      writer.write("if (temp != null)\n");
      writer.write("((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text()).add(temp.text);\n");
      writer.write("}\n");
    }
    else {
      if (hasSuperEnumerations)
        writer.write("@" + Override.class.getName() + "\n");
      writer.write("public int ordinal()\n");
      writer.write("{\n");
      writer.write("final " + (hasSuperEnumerations ? ((ExtensiblePlan)plan).getSuperClassNameWithoutType() + "." : "") + "Enum enm = Enum.values().get(text());\n");
      writer.write("return enm != null ? enm.ordinal() : -1;\n");
      writer.write("}\n");

      writer.write("public " + plan.getClassSimpleName() + "(final " + enmClassName + " enm)\n");
      writer.write("{\n");
      if (!hasSuperEnumerations)
        writer.write("super(enm.text());\n");
      else
        writer.write("super(enm);\n");
      writer.write("}\n");
    }
  }

  protected static void getEncodeDecode(final StringWriter writer, final SimpleTypePlan<?> plan) {
    // DECODE & ENCODE
    if (plan.isList()) {
      writer.write("@" + Override.class.getName() + "\n");
      writer.write("protected void _$$decode(final " + Element.class.getName() + " node, " + String.class.getName() + " value)\n");
      writer.write("{\n");
      writer.write("if (value == null || value.length() == 0)\n");
      writer.write("return;\n");
      writer.write("super.text(new " + plan.getNativeItemClassNameImplementation() + "());\n");
      writer.write(StringTokenizer.class.getName() + " tokenizer = new " + StringTokenizer.class.getName() + "(value);\n");
      writer.write("while (tokenizer.hasMoreTokens())\n");
      String factoryEntry = "tokenizer.nextToken()";
      if (plan.getNativeFactory() != null)
        factoryEntry = plan.getNativeFactory() + "(" + factoryEntry + ")";

      writer.write("((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text()).add(" + factoryEntry + ");\n");
      writer.write("}\n");

      writer.write("@" + Override.class.getName() + "\n");
      writer.write("protected " + String.class.getName() + " _$$encode(final " + Element.class.getName() + " parent) throws " + MarshalException.class.getName() + "\n");
      writer.write("{\n");
      writer.write("return super.text() != null && ((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text()).size() != 0 ? " + CollectionUtil.class.getName() + ".toString((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text(), \" \") : null;\n");
      writer.write("}\n");
    }
  }

  @Override
  protected void appendRegistration(final StringWriter writer, final T plan, final Plan<?> parent) {
    writer.write("_$$registerType(" + plan.getClassName(parent) + ".NAME, " + plan.getClassName(parent) + ".class);\n");
    writer.write("_$$registerSchemaLocation(" + plan.getClassName(parent) + ".NAME.getNamespaceURI(), " + plan.getClassSimpleName() + ".class, \"" + plan.getXsdLocation() + "\");\n");
  }

  @Override
  protected void appendDeclaration(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a declaration");
  }

  @Override
  protected void appendGetMethod(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a get method");
  }

  @Override
  protected void appendSetMethod(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a set method");
  }

  @Override
  protected void appendMarshal(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a marshal method");
  }

  @Override
  protected void appendParse(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a parse method");
  }

  @Override
  public void appendCopy(final StringWriter writer, final T plan, final Plan<?> parent, final String variable) {
    throw new CompilerFailureException("simpleType cannot have a copy statement");
  }

  @Override
  protected void appendEquals(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a equals statement");
  }

  @Override
  protected void appendHashCode(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a hashCode statement");
  }

  @Override
  protected void appendClone(final StringWriter writer, final T plan, final Plan<?> parent) {
    throw new CompilerFailureException("simpleType cannot have a clone statement");
  }

  protected static void appendPattern(final StringWriter writer, final Collection<PatternPlan> patterns) {
    if (patterns == null || patterns.size() == 0)
      return;

    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected " + String.class.getName() + "[] _$$getPattern()\n");
    writer.write("{\n");
    writer.write("return new " + String.class.getName() + "[]\n");
    writer.write("{\n");
    final StringBuilder buf = new StringBuilder();
    for (final PatternPlan pattern : patterns)
      buf.append(",\n\"").append(pattern.getValue()).append("\"");
    writer.write(buf.substring(2) + "\n");
    writer.write("};\n");
    writer.write("}\n");
  }

  @Override
  protected void appendClass(final StringWriter writer, final T plan, final Plan<?> parent) {
    if (plan.getName() == null)
      throw new CompilerFailureException("Why are we trying to write a final class that has no name?");

    writeQualifiedName(writer, plan);
    writer.write("public abstract static class " + plan.getClassSimpleName() + " extends " + plan.getSuperClassNameWithType() + " implements " + SimpleType.class.getName() + "\n");
    writer.write("{\n");

    writer.write("private static final " + QName.class.getName() + " NAME = getClassQName(" + plan.getClassName(parent) + ".class);\n");

    // ID LOOKUP
    writeIdLookup(writer, plan, parent);

    // FACTORY METHOD
    writer.write("protected static " + plan.getClassSimpleName() + " newInstance(final " + plan.getBaseNonXSTypeClassName() + " inherits)\n");
    writer.write("{\n");
    writer.write("return new " + plan.getClassName(parent) + "()\n");
    writer.write("{\n");
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected " + plan.getBaseNonXSTypeClassName() + " inherits()\n");
    writer.write("{\n");
    writer.write("return inherits;\n");
    writer.write("}\n");
    writer.write("};\n");
    writer.write("}\n");

    // DOCUMENTATION
    writer.write(plan.getDocumentation());

    // COPY CONSTRUCTOR
    writer.write("public " + plan.getClassSimpleName() + "(" + plan.getClassName(null) + " copy)\n");
    writer.write("{\n");
    writer.write("super(copy);\n");
    writer.write("}\n");

    // ENUMERATIONS CONSTRUCTOR
    getRestrictions(writer, plan);

    // NATIVE CONSTRUCTORS
    getNativeConstructors(writer, plan);

    // DEFAULT CONSTRUCTOR
    if (!plan.hasEnumerations()) {
      // DOCUMENTATION
      writer.write(plan.getDocumentation());

      writer.write("public ");
    }
    else
      writer.write("protected ");
    writer.write(plan.getClassSimpleName() + "()\n");
    writer.write("{\n");
    writer.write("super();\n");
    writer.write("}\n");

    if (plan.getNativeItemClassNameInterface() != null) {
      writer.write("@" + Override.class.getName() + "\n");
      writer.write("public " + plan.getNativeItemClassNameInterface() + " text()\n");
      writer.write("{\n");
      if (!Object.class.getName().equals(plan.getNativeItemClassNameInterface()))
        writer.write("return (" + plan.getNativeItemClassNameInterface() + ")super.text();\n");
      else
        writer.write("return super.text();\n");
      writer.write("}\n");

      if (plan.isList()) {
        writer.write("public " + plan.getNativeItemClassName() + " text(final int index)\n");
        writer.write("{\n");
        writer.write("final " + plan.getNativeItemClassNameInterface() + " values = text();\n");
        writer.write("return values != null && -1 < index && index < values.size() ? values.get(index) : null;\n");
        writer.write("}\n");
      }

      if (plan.hasEnumerations()) {
        if (plan.isList()) {
          writer.write("public <E extends " + List.class.getName() + "<" + plan.getClassName(parent) + ".Enum> & " + Serializable.class.getName() + ">void text(final E enm)\n");
          writer.write("{\n");
          writer.write("super.text(new " + plan.getNativeItemClassNameImplementation() + "());\n");
          writer.write("for(" + plan.getClassName(parent) + ".Enum temp : enm)\n");
          writer.write("if (temp != null)\n");
          writer.write("((" + List.class.getName() + "<" + plan.getNativeItemClassName() + ">)super.text()).add(temp.text);\n");
          writer.write("}\n");
        }
        else {
          writer.write("public void text(final Enum enm)\n");
          writer.write("{\n");
          writer.write("super.text(enm.text);\n");
          writer.write("}\n");

          if (plan.isUnionWithNonEnumeration()) {
            writer.write("@" + Override.class.getName() + "\n");
            writer.write("public void text(final " + plan.getNativeNonEnumItemClassNameInterface() + " text)\n");
            writer.write("{\n");
            writer.write("super.text(text);\n");
            writer.write("}\n");
          }
        }
      }
      else {
        // FIXME: This misses some @Override(s) for situations that inherit from xs types, cause the type of the parameter to the text() method is not known here
        if (parent != null && ((SimpleTypePlan<?>)parent).getNativeItemClassNameInterface().equals(plan.getNativeItemClassNameInterface()))
          writer.write("@" + Override.class.getName() + "\n");

        if (plan.getNativeItemClassNameInterface().contains(List.class.getName()))
          writer.write("public <T extends " + plan.getNativeItemClassNameInterface() + " & " + Serializable.class.getName() + ">void text(final T text)\n");
        else
          writer.write("public void text(final " + plan.getNativeItemClassNameInterface() + " text)\n");
        writer.write("{\n");
        writer.write("super.text(text);\n");
        writer.write("}\n");

        if (plan.getNativeItemClassName() == null && XSTypeDirectory.ANYSIMPLETYPE.getNativeBinding().getName().equals(plan.getBaseXSItemTypeName())) {
          writer.write("@" + Override.class.getName() + "\n");
          writer.write("public <T extends " + List.class.getName() + "<" + plan.getNativeItemClassNameInterface() + "> & " + Serializable.class.getName() + ">void text(final T text)\n");
          writer.write("{\n");
          writer.write("super.text(text);\n");
          writer.write("}\n");
        }
      }
    }

    // DECODE & ENCODE
    getEncodeDecode(writer, plan);

    // INHERITS
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected abstract " + plan.getBaseNonXSTypeClassName() + " inherits();\n");

    // ID
    if (plan.getId() != null) {
      writer.write("@" + Override.class.getName() + "\n");
      writer.write("public " + String.class.getName() + " id()\n");
      writer.write("{\n");
      writer.write("return \"" + plan.getId() + "\";\n");
      writer.write("}\n");
    }

    // NAME
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("public " + QName.class.getName() + " name()\n");
    writer.write("{\n");
    writer.write("return name(_$$inheritsInstance());\n");
    writer.write("}\n");

    // TYPE
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("public " + QName.class.getName() + " type()\n");
    writer.write("{\n");
    writer.write("return NAME;\n");
    writer.write("}\n");

    // PATTERN
    appendPattern(writer, plan.getPatterns());

    // MARSHAL
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected " + Attr.class.getName() + " marshalAttr(final " + String.class.getName() + " name, final " + Element.class.getName() + " parent) throws " + MarshalException.class.getName() + "\n");
    writer.write("{\n");
    writer.write("return super.marshalAttr(name, parent);\n");
    writer.write("}\n");
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected " + Element.class.getName() + " marshal() throws " + MarshalException.class.getName() + "\n");
    writer.write("{\n");
    writer.write(Element.class.getName() + " root = createElementNS(name().getNamespaceURI(), name().getLocalPart());\n");
    writer.write("return marshal(root, name(), type(_$$inheritsInstance()));\n");
    writer.write("}\n");
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("protected " + Element.class.getName() + " marshal(final " + Element.class.getName() + " parent, final " + QName.class.getName() + " name, final " + QName.class.getName() + " type) throws " + MarshalException.class.getName() + "\n");
    writer.write("{\n");
    writer.write("return super.marshal(parent, name, type);\n");
    writer.write("}\n");

    // IS_NULL
    //writeIsNull(writer, plan);

    // CLONE
    writer.write("@" + Override.class.getName() + "\n");
    writer.write("public " + plan.getClassName(parent) + " clone()\n");
    writer.write("{\n");
    writer.write("return (" + plan.getClassName(parent) + ")super.clone();\n");
    writer.write("}\n");

    // EQUALS
//    writer.write("@" + Override.class.getName() + "\n");
//    writer.write("public boolean equals(final " + Object.class.getName() + " obj)\n");
//    writer.write("{\n");
//    // NOTE: This is not checking whether getTEXT() is equal between this and obj
//    // NOTE: because this final class does not contain the text field.
//    writer.write("return super.equals(obj);\n");
//    writer.write("}\n");

    // HASHCODE
//    writer.write("@" + Override.class.getName() + "\n");
//    writer.write("public int hashCode()\n");
//    writer.write("{\n");
//    writer.write("int hashCode = super.hashCode();\n");
//    // NOTE: This is not checking whether getTEXT() is equal between this and obj
//    // NOTE: because this final class does not contain the text field.
//    writer.write("return hashCode;\n");
//    writer.write("}\n");

    writer.write("}\n");
  }
}