select `owner_id_pk`, `owner_name` from `owner` where `owner_id_pk` = (select `cfg_owner_id` from `config`);
