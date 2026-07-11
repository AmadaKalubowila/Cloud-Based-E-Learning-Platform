# Running Local vs Production

The app now has two Spring profiles: `local` (default) and `prod`.
Which one runs is controlled entirely by the `SPRING_PROFILES_ACTIVE`
environment variable — no code or jar changes needed to switch.

- `application.properties` — shared settings, no secrets
- `application-local.properties` — local MySQL, verbose logging, ddl-auto=update
- `application-prod.properties` — RDS over SSL, quieter logging, values have no defaults so it fails fast if a secret is missing

## Local development (your machine)

1. Make sure MySQL is running locally and you have a user/password for it.
2. `cp .env.example .env` and fill in your local MySQL password (and JWT_SECRET
   if you want, dev default is fine).
3. Load the `.env` file into your shell, then run:

   ```bash
   set -a && source .env && set +a
   ./mvnw spring-boot:run
   ```

   Or, if you use IntelliJ: Run > Edit Configurations > Environment variables,
   paste the contents of `.env`, leave `SPRING_PROFILES_ACTIVE` unset/`local`.

   Since `spring.profiles.active` defaults to `local`, you can even skip
   `SPRING_PROFILES_ACTIVE` entirely for local runs.

## Production (AWS EC2)

One-time server setup:

```bash
sudo useradd -r -s /sbin/nologin elearning
sudo mkdir -p /opt/elearning
sudo cp target/elearning-0.0.1-SNAPSHOT.jar /opt/elearning/elearning.jar
sudo cp deploy/elearning.env.example /opt/elearning/elearning.env
sudo nano /opt/elearning/elearning.env      # fill in real prod values
sudo chown -R elearning:elearning /opt/elearning
sudo chmod 600 /opt/elearning/elearning.env

sudo cp deploy/elearning.service /etc/systemd/system/elearning.service
sudo systemctl daemon-reload
sudo systemctl enable --now elearning
```

Every future deploy is just:

```bash
./mvnw clean package -DskipTests
scp target/elearning-0.0.1-SNAPSHOT.jar ec2-user@<host>:/tmp/elearning.jar
ssh ec2-user@<host> "sudo mv /tmp/elearning.jar /opt/elearning/elearning.jar && sudo systemctl restart elearning"
```

Check it's healthy: `curl http://<host>:8088/actuator/health` and
`sudo journalctl -u elearning -f` for logs.

**Security note:** `/opt/elearning/elearning.env` never goes in git. It
lives only on the EC2 box, root/elearning-owned, mode 600. Prefer
attaching an IAM role to the EC2 instance and leaving
`AWS_ACCESS_KEY_ID`/`AWS_SECRET_ACCESS_KEY` blank in that file — the app
already falls back to the instance role automatically (see `S3Config.java`).

## Rotate the leaked credentials

These were committed to the repo and must be rotated before going to
production, if you haven't already:
1. AWS IAM console → deactivate/delete access key `AKIA3JXJUUVLF54RO2XI`,
   create a new one (or, better, switch to an EC2 instance role and skip
   static keys altogether).
2. RDS console → modify the `admin` user's password.
3. Google Account → App Passwords → revoke the old Gmail app password,
   generate a new one for `MAIL_PASSWORD`.
4. Optional but recommended: scrub the old secrets from git history with
   `git filter-repo` or the BFG Repo-Cleaner, then force-push, since
   `git log` shows they've been in commits since early in the project.
