select `cred_id_pk`, `cred_service`, `cred_login`, `cred_password`, `cred_owner_id`, `owner_name`
from `credentials`
left outer join `owner` on `cred_owner_id` = `owner_id_pk`
where `cred_service` like "%s%%" and `cred_owner_id` like "%s"
order by `cred_service`