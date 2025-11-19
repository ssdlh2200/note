select p.firstName, p.lastName, a.city, a.state from person p LEFT OUTER JOIN address a
    ON p.personId = a.personId;


