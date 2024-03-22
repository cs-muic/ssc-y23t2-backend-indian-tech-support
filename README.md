# README.MD

# Backend

## APIs

### Transaction Blueprints (Shortcuts (Recurrings / Favorites))

- POST "/api/transaction-blueprints/recurring/create"
    - Description: \
        create new recurring shotcut
    - request parameter: \
        - "tagId":
            - requirement: \
                REQUIRE
            - description: \
                tag id of transaction
            - notes: \
                a
        - "tagId2":
            - requirement: \
                REQUIRE
            - description: \
                tag id 2 of transaction
            - notes: \
                a
        - "type":
            - requirement: \
                REQUIRE
            - description: \
                type of transaction \
                "INCOME" or "EXPENSE"
            - notes: \
                a
        - "shortcutType":
            - requirement: \
                REQUIRE
            - description: \
                type of transaction blueprints \
                "RECURRING" or "FAVORITES"
            - notes: \
                a
        - "notes":
            - requirement: \
                REQUIRE
            - description: \
                notes of transaction
            - notes: \
                a
        - "value":
            - requirement: \
                REQUIRE
            - description: \
                value of transaction
            - notes: \
                a
        - "dateOfMonthRecurring":
            - requirement: \
                REQUIRE if type == "RECURRING"
            - description: \
                date of month for recurring transaction
            - notes: \
                a
        <!-- - "tagId":
            - requirement: \
                REQUIRE
            - description: \
                tag id of transaction
            - notes: \
                a -->