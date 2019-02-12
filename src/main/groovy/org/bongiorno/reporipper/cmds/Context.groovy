package org.bongiorno.reporipper.cmds

import groovy.transform.Immutable;

import java.util.Map;
import java.util.Set;

@Immutable
class Context {
    final String[]            args
    final Map<String, String> options
    final Set<String>         switches
    final Map<String, ?>      config
}
