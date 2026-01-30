package com.freelance.app.domain;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthorityCallback implements AfterSaveCallback<Authority>, AfterConvertCallback<Authority> {

    @Override
    public @NotNull Publisher<Authority> onAfterConvert(Authority entity, @NotNull SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public @NotNull Publisher<Authority> onAfterSave(Authority entity, @NotNull OutboundRow outboundRow, @NotNull SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
