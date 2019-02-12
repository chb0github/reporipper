package org.bongiorno.reporipper.scms

import groovy.transform.Immutable

@Immutable
class Project {
    final String name
    final String key
    final String description
    final byte[] avatar
}
