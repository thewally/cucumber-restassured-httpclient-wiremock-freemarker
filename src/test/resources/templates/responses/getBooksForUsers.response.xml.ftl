<getBooksForUsersResponse>
    <users>
        <#list users as user>
        <user>
            <id>${user.id}</id>
            <lastname>${user.lastName}</lastname>
            <firstname>${user.firstName}</firstname>
            <streetname>${user.streetName}</streetname>
            <housenumber>${user.houseNumber}</housenumber>
            <postalcode>${user.postalCode}</postalcode>
            <city>${user.city}</city>
            <books>
                <#list books as book>
                <book>
                    <id>${book.id}</id>
                    <title>${book.title}</title>
                    <author>${book.author}</author>
                    <year>${book.year}</year>
                </book>
                </#list>
            </books>
        </user>
        </#list>
    </users>
</getBooksForUsersResponse>