# XeonKitPvP Core 2.1.0

Core KitPvP modulare per Spigot/Bukkit. Compilato contro Spigot API 1.8.8 e scritto senza NMS per mantenere un'unica base compatibile dove l'API lo consente.

## Moduli principali

- ACF (Aikar Command Framework) per comandi, sottocomandi, completamenti e permessi.
- Livelli + XP + Prestige automatico.
- Quest giornaliere e settimanali con reset di periodo e ricompense.
- Anti-farm/anti-abuse basato su coppia di giocatori, finestra temporale e limite di kill ricompensate.
- Combat-log con tag, blocco comandi, conteggio dell'avversario e penalità alla disconnessione.
- Chat Tags configurabili.
- Kill Effects configurabili, senza NMS.
- Shop GUI per tag e kill effects.
- PlaceholderAPI, Vault e TAB come hook opzionali.

## Comandi giocatore

- `/kit` / `/kit <kit>`
- `/stats [player]`
- `/top <kills|coins|streak|level|xp|prestige> [page]`
- `/quests`
- `/level` o `/prestige`
- `/shop`
- `/tag [id]`
- `/killeffect [id]`
- `/bounty <player> [amount]`
- `/spawn`
- `/gang ...`

## Comandi admin

- `/kitpvp admin money <set|give|take> <player> <amount>`
- `/kitpvp admin stats <set|add|reset> <player> <stat> [value]`
- `/kitpvp admin prestige <set|add|reset> <player> [value]`
- `/kitpvp admin kit <give|take> <player> <kit>`
- `/kitpvp admin cosmetic tag give <player> <id>`
- `/kitpvp admin cosmetic effect give <player> <id>`
- `/kitpvp admin quest <reset|complete> <player> <daily|weekly> <id>`
- `/kitpvp admin antifarm clear`
- `/kitpvp admin combat clear <player>`
- `/kitpvp admin player reset <player>`
- `/kitpvp admin arena <add|remove|list> [world]`
- `/kitpvp admin heal|feed|clear <player>`
- `/kitpvp reload`
- `/kitpvp debug`

## Configurazione

```text
plugins/XeonKitPvP/
├── config.yml
├── lang.yml
├── kits.yml
├── quests.yml
├── cosmetics.yml
├── shop.yml
├── players.yml
└── gangs.yml
```

### Anti-farm

```yaml
anti-farm:
  enabled: true
  pair-window-seconds: 600
  max-rewarded-kills-per-window: 3
  minimum-kill-delay-seconds: 20
  block-statistics: false
```

Per default le kill sospette non ricevono coin/XP/quest/bounty/kill-effect, mentre le statistiche restano conteggiate. `block-statistics: true` rende il blocco più severo.

### Level + Prestige

Il player sale di livello usando XP. Al raggiungimento del livello massimo, se il Prestige è abilitato, viene creato automaticamente il Prestige successivo fino a `max-prestige`. Ogni Prestige può aumentare il requisito XP attraverso `prestige-xp-multiplier`.

### Combat-log

Il combat tag memorizza l'ultimo avversario. Se un giocatore esce durante il tag, il core registra la morte e, quando l'avversario è ancora online, assegna la kill attraverso la stessa pipeline del normale PvP. I comandi configurati in `combat-log.blocked-commands` vengono rifiutati durante il tag.

## PlaceholderAPI

Oltre ai placeholder esistenti sono disponibili:

- `%xeonkitpvp_prestige%`
- `%xeonkitpvp_tag%`
- `%xeonkitpvp_killeffect%`

## Build

Richiede Maven e JDK compatibile. Il bytecode è Java 8.

```bash
mvn clean package
```

Output:

```text
target/XeonKitPvP-2.1.0.jar
```
