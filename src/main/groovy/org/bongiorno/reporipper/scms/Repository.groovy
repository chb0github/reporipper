package org.bongiorno.reporipper.scms

import groovy.transform.Immutable

/**
 * @author cbongiorno on 11/2/18.
 */
@Immutable
class Repository {

    final String name
    final String key
    final String description
    final Map<String,String> clone

}