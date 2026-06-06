CREATE TABLE accounts(
  id UUID PRIMARY KEY,
  owner_id UUID NOT NULL,
  type varchar(50) NOT NULL,
  status varchar(50) NOT NULL,
  currency varchar(10) NOT NULL,
  balance NUMERIC(19, 4) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);