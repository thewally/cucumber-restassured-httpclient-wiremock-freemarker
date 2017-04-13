<getBooksForUsersResponse>
    <#if users??>
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
            <#if user.books??>
            <books>
                <#list user.books as book>
                <book>
                    <id>${book.id}</id>
                    <title>${book.title}</title>
                    <author>${book.author}</author>
                    <year>${book.year}</year>
                </book>
                </#list>
            </books>
            <#else>
            <books/>
            </#if>
        </user>
        </#list>
    </users>
    <#else>
    <users/>
    </#if>
</getBooksForUsersResponse>
