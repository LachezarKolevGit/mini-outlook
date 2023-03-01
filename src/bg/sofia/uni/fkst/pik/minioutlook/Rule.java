package bg.sofia.uni.fkst.pik.minioutlook;

import java.util.Set;

public record Rule(Set<String> subjectIncludes, Set<String> subjectOrBodyIncludes, Set<String> recipientsIncludes, String sender, int numberOfRuleConditions) {
}
