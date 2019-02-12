package org.bongiorno.reporipper.scms

import groovy.transform.TupleConstructor

@TupleConstructor
class ImmutableScm implements Scm {


    @Delegate(excludes = ['user', 'pass', 'url'])
    final Scm delegate

    @Override
    String toString() {
        delegate.name
    }
}
