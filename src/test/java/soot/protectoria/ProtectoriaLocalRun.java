package soot.protectoria;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;

import soot.Pack;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.Transform;
import soot.jbco.jimpleTransformations.ClassRenamer;
import soot.jbco.jimpleTransformations.CollectConstants;
import soot.jbco.jimpleTransformations.FieldRenamer;
import soot.jbco.jimpleTransformations.MethodRenamer;
import soot.options.Options;

/**
 * @author p.nesterovich
 * @since 29/08/18
 */
public class ProtectoriaLocalRun {

  @Test
  public void main() throws IOException {

    final Path rootPath = Paths.get("/Users/paul/.protectoria/ANDROID");

    // Those options should be configured even before obfuscation packs!!!
    // configure options

    // Options.v().set_exclude(new ArrayList<>(params.getExclude()));
    Options.v().set_process_dir(Collections.singletonList(rootPath.resolve("compiled-soot").toString()));
    Options.v().set_soot_classpath(String.join(File.pathSeparator,
        Files.list(rootPath.resolve("libs")).map(Path::toAbsolutePath).map(Path::toString).collect(Collectors.toList())));
    Options.v().set_output_dir(rootPath.resolve("result-" + System.currentTimeMillis()).toString());
    Options.v().set_output_format(Options.output_format_class);
    // Options.v().set_output_format(Options.output_format_jimple);
    Options.v().set_java_version(Options.java_version_1_7);
    Options.v().set_whole_program(true);
    Options.v().set_allow_phantom_refs(true);

    // Options.v().set_verbose(true);

    // fix soot warnings by providing proper phase options
    PhaseOptions.v().setPhaseOption("cg", "verbose");
    PhaseOptions.v().setPhaseOption("cg", "all-reachable:true"); // to create full call-graph and avoid errors
    PhaseOptions.v().setPhaseOption("cg", "safe-newinstance");
    PhaseOptions.v().setPhaseOption("cg", "types-for-invoke");
    PhaseOptions.v().setPhaseOption("cg", "safe-forname");
    PhaseOptions.v().setPhaseOption("cg", "jdkver:7");

    // PhaseOptions.v().setPhaseOption("wjtp", "rename-fields:true");

    // should be initialized after Options.v()
    Pack wjtp = PackManager.v().getPack("wjtp");
    Pack jtp = PackManager.v().getPack("jtp");
    Pack bb = PackManager.v().getPack("bb");

    wjtp.add(new Transform(ClassRenamer.name, ClassRenamer.v()));
//    wjtp.add(new Transform(MethodRenamer.name, MethodRenamer.v()));

    FieldRenamer.v().setRenameFields(true);
    wjtp.add(new Transform(FieldRenamer.name, FieldRenamer.v()));

//    wjtp.add(new Transform(CollectConstants.name, new CollectConstants()));
    //
    // wjtp.add(new Transform(LibraryMethodWrappersBuilder.name, new LibraryMethodWrappersBuilder()));

    // jtp.add(new Transform(CollectJimpleLocals.name, new CollectJimpleLocals()));
    // jtp.insertBefore(new Transform(GotoInstrumenter.name, new GotoInstrumenter()), CollectJimpleLocals.name);

    // bb.add(new Transform(
    // RemoveRedundantPushStores.name, new RemoveRedundantPushStores()));

    // bb.insertBefore(new Transform(
    // FixUndefinedLocals.name, new FixUndefinedLocals()), "bb.lso");
    //
    // bb.insertBefore(new Transform(
    // "bb.printout", new BAFPrintout("bb.printout", true)), FixUndefinedLocals.name);

    // FieldRenamer.rename_fields = true;
    // wjtp.add(new Transform(
    // FieldRenamer.name, new FieldRenamer()));

    Scene.v().loadNecessaryClasses();

    // run
    PackManager.v().runPacks();

    // for (SootClass appClass : Scene.v().getApplicationClasses()) {
    //
    // for (SootMethod method : appClass.getMethods()) {
    // if (method.getTags() != null && !method.getTags().isEmpty()) {
    // for (Tag tag : method.getTags()) {
    // System.out.println(tag.getName());
    // }
    //
    // }
    // }
    //
    // }

    PackManager.v().writeOutput();

    // StringWriter writer = new StringWriter();
    // PrintWriter writerOut = new PrintWriter(writer);
    // Printer.v().printTo(Scene.v().getSootClass("com.protectoria.psa.dex.core.eventbus.EventType"), writerOut);
    // Printer.v().printTo(Scene.v().getSootClass("com.protectoria.psa.dex.auth.core.actions.BaseUiAction"), writerOut);
    // Printer.v().printTo(Scene.v().getSootClass("com.protectoria.psa.dex.core.AbstractCodeBlockController"), writerOut);
    // System.out.println(writer);
  }

}
